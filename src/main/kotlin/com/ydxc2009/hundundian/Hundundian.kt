package com.ydxc2009.hundundian

import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class Hundundian : JavaPlugin(), Listener {

    var chaosPoint: Location? = null
        private set
    
    private var mobSpawnTask: BukkitRunnable? = null
    private var soundTask: BukkitRunnable? = null
    private val DETECTOR_RANGE = 30.0
    private val MOB_SPAWN_RADIUS = 15.0
    private val MOB_SPAWN_INTERVAL = 300L
    private val SOUND_MAX_DISTANCE = 50.0
    private val SOUND_INTERVAL = 60L
    private val ANNOUNCE_TIMES = 3
    private val ANNOUNCE_INTERVAL = 60L

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        logger.info("混沌点插件已启动！")
    }

    override fun onDisable() {
        stopChaosPoint()
        logger.info("混沌点插件已关闭！")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (command.name.lowercase()) {
            "hundundian" -> {
                if (!sender.hasPermission("hundundian.admin")) {
                    sender.sendMessage("§c你没有权限使用此命令！")
                    return true
                }
                
                sender.sendMessage("§6§l=== 混沌点插件指令 ===")
                sender.sendMessage("§e/hundundian §7- 显示所有指令 §c[管理员]")
                sender.sendMessage("§e/setpoint §7- 在当前位置设置混沌点 §c[管理员]")
                sender.sendMessage("§e/closepoint §7- 关闭当前的混沌点 §c[管理员]")
                sender.sendMessage("§e/pointhint §7- 发送混沌点位置提示 §c[管理员]")
                sender.sendMessage("§e/broadcast §7- 广播混沌点开启提示 §c[管理员]")
                sender.sendMessage("§e/detector §7- 获得羽毛探测器 §c[管理员]")
                return true
            }
            
            "setpoint" -> {
                if (!sender.hasPermission("hundundian.admin")) {
                    sender.sendMessage("§c你没有权限使用此命令！")
                    return true
                }
                
                if (sender !is Player) {
                    sender.sendMessage("§c此命令只能由玩家执行！")
                    return true
                }
                
                setChaosPoint(sender.location)
                announcePointCreation()
                return true
            }
            
            "closepoint" -> {
                if (!sender.hasPermission("hundundian.admin")) {
                    sender.sendMessage("§c你没有权限使用此命令！")
                    return true
                }
                
                closeChaosPoint()
                sender.sendMessage("§6[混沌点] §a混沌点已关闭！")
                return true
            }
            
            "pointhint" -> {
                if (!sender.hasPermission("hundundian.admin")) {
                    sender.sendMessage("§c你没有权限使用此命令！")
                    return true
                }
                
                if (chaosPoint == null) {
                    sender.sendMessage("§c当前没有开启的混沌点！")
                    return true
                }
                
                sendLocationHint()
                return true
            }
            
            "broadcast" -> {
                if (!sender.hasPermission("hundundian.admin")) {
                    sender.sendMessage("§c你没有权限使用此命令！")
                    return true
                }
                
                if (chaosPoint == null) {
                    sender.sendMessage("§c当前没有开启的混沌点！")
                    return true
                }
                
                announcePointCreation()
                sender.sendMessage("§6[混沌点] §a已发送混沌点开启广播")
                return true
            }
            
            "detector" -> {
                if (!sender.hasPermission("hundundian.admin")) {
                    sender.sendMessage("§c你没有权限使用此命令！")
                    return true
                }
                
                if (sender !is Player) {
                    sender.sendMessage("§c此命令只能由玩家执行！")
                    return true
                }
                
                giveDetector(sender)
                sender.sendMessage("§6[混沌点] §a你获得了羽毛探测器！")
                return true
            }
        }
        return false
    }

    private fun setChaosPoint(location: Location) {
        chaosPoint = location.clone()
        startMobSpawning()
        startSoundEffect()
    }

    private fun closeChaosPoint() {
        chaosPoint = null
        stopChaosPoint()
    }
    
    private fun stopChaosPoint() {
        stopMobSpawning()
        stopSoundEffect()
    }

    private fun sendLocationHint() {
        chaosPoint?.let { loc ->
            val x = loc.blockX
            val y = loc.blockY
            val z = loc.blockZ
            Bukkit.broadcastMessage("§6[混沌点] §c异界门正在 §e$x $y $z §c源源不断散播着热浪...")
        }
    }
    
    private fun announcePointCreation() {
        var count = 0
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("§6[混沌点] §c新的炙热裂缝已生成，可使用 §e羽毛探测器 §c寻找裂缝位置")
                count++
                if (count >= ANNOUNCE_TIMES) {
                    cancel()
                }
            }
        }.runTaskTimer(this, 0L, ANNOUNCE_INTERVAL)
    }

    private fun giveDetector(player: Player) {
        val detector = ItemStack(Material.FEATHER)
        val meta = detector.itemMeta!!
        
        meta.setDisplayName("§e§l羽毛探测器")
        meta.lore = listOf(
            "§7右键使用来探测混沌点位置",
            "§c像末影之眼一样指引方向"
        )
        meta.addEnchant(Enchantment.INFINITY, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        
        detector.itemMeta = meta
        player.inventory.addItem(detector)
    }

    @EventHandler
    fun onPlayerUseDetector(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val item = event.item ?: return
        if (item.type != Material.FEATHER) return
        
        val meta = item.itemMeta ?: return
        if (meta.displayName != "§e§l羽毛探测器") return

        val player = event.player
        
        if (chaosPoint == null) {
            player.sendMessage("§6[混沌点] §c当前没有开启的混沌点！")
            return
        }

        event.isCancelled = true
        
        if (item.amount > 1) {
            item.amount--
        } else {
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
        }

        player.world.playSound(player.location, Sound.ENTITY_ENDER_EYE_LAUNCH, 1.0f, 1.0f)
        val direction = chaosPoint!!.toVector().subtract(player.location.toVector()).normalize()
        launchDetectorFeather(player, direction)
    }

    private fun launchDetectorFeather(player: Player, direction: Vector) {
        val startLoc = player.eyeLocation.clone().add(direction.clone().multiply(0.5))
        val feather = player.world.dropItem(startLoc, ItemStack(Material.FEATHER))
        
        feather.pickupDelay = Int.MAX_VALUE
        feather.velocity = direction.clone().multiply(0.8).setY(0.5)
        
        object : BukkitRunnable() {
            var ticks = 0
            
            override fun run() {
                ticks++
                
                if (!feather.isValid || ticks > 60) {
                    val loc = feather.location
                    loc.world?.playSound(loc, Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f)
                    loc.world?.spawnParticle(Particle.ITEM, loc, 20, 0.2, 0.2, 0.2, 0.1, ItemStack(Material.FEATHER))
                    feather.remove()
                    cancel()
                    return
                }
                
                feather.world.spawnParticle(
                    Particle.FLAME,
                    feather.location,
                    2,
                    0.05, 0.05, 0.05,
                    0.01
                )
                
                if (feather.location.distance(chaosPoint!!) < 5.0) {
                    val loc = feather.location
                    loc.world?.playSound(loc, Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f)
                    loc.world?.spawnParticle(Particle.ITEM, loc, 20, 0.2, 0.2, 0.2, 0.1, ItemStack(Material.FEATHER))
                    feather.remove()
                    cancel()
                }
            }
        }.runTaskTimer(this, 0L, 1L)
    }

    private fun startMobSpawning() {
        stopMobSpawning()
        
        mobSpawnTask = object : BukkitRunnable() {
            override fun run() {
                val point = chaosPoint ?: run {
                    cancel()
                    return
                }
                
                val nearbyPlayers = point.world?.players?.filter { player ->
                    player.location.distance(point) <= MOB_SPAWN_RADIUS && 
                    player.gameMode != GameMode.SPECTATOR
                } ?: emptyList()
                
                if (nearbyPlayers.isEmpty()) return
                
                nearbyPlayers.forEach { player ->
                    spawnPiglin(point, player)
                }
            }
        }
        
        mobSpawnTask?.runTaskTimer(this, MOB_SPAWN_INTERVAL, MOB_SPAWN_INTERVAL)
    }

    private fun stopMobSpawning() {
        mobSpawnTask?.cancel()
        mobSpawnTask = null
    }
    
    private fun startSoundEffect() {
        stopSoundEffect()
        
        soundTask = object : BukkitRunnable() {
            override fun run() {
                val point = chaosPoint ?: run {
                    cancel()
                    return
                }
                
                val world = point.world ?: run {
                    cancel()
                    return
                }
                
                world.players.forEach { player ->
                    val distance = player.location.distance(point)
                    if (distance <= SOUND_MAX_DISTANCE) {
                        val volume = ((SOUND_MAX_DISTANCE - distance) / SOUND_MAX_DISTANCE).toFloat()
                        player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, volume, 1.0f)
                    }
                }
            }
        }
        
        soundTask?.runTaskTimer(this, 0L, SOUND_INTERVAL)
    }
    
    private fun stopSoundEffect() {
        soundTask?.cancel()
        soundTask = null
    }

    private fun spawnPiglin(point: Location, nearPlayer: Player) {
        val world = point.world ?: return
        
        val angle = Math.random() * 2 * Math.PI
        val distance = Math.random() * 8 + 5
        val x = point.x + cos(angle) * distance
        val z = point.z + sin(angle) * distance
        val y = world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble() + 1
        val spawnLoc = Location(world, x, y, z)
        
        if (!spawnLoc.block.type.isAir || !spawnLoc.clone().add(0.0, 1.0, 0.0).block.type.isAir) {
            return
        }
        
        val zombiePiglin = world.spawnEntity(spawnLoc, EntityType.ZOMBIFIED_PIGLIN) as org.bukkit.entity.PigZombie
        zombiePiglin.setTarget(nearPlayer)
        zombiePiglin.lootTable = null
        
        world.spawnParticle(Particle.FLAME, spawnLoc, 30, 0.5, 0.5, 0.5, 0.1)
        world.playSound(spawnLoc, Sound.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT, 1.0f, 0.8f)
    }
}
