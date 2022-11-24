package main.parsehandler;

import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ZombieParser {
    public static boolean isZombie(@NotNull Entity entity) {
        return entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.HUSK) || entity.getType().equals(EntityType.ZOMBIE_VILLAGER);
    }
    public static void asCustomZombie(@NotNull LivingEntity zombie) {
        zombie.setRemoveWhenFarAway(false);
        zombie.setPersistent(true);
        zombie.addPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false), new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, false, false)));
        switch (zombie.getType()) {
            case ZOMBIE -> {
                Zombie z = ((Zombie) zombie);
                z.setArmsRaised(true);
                z.setCanBreakDoors(false);
                z.setCanPickupItems(false);
                z.setTarget(PlayerParser.getNearestPlayer(z));
            } case HUSK -> {
                Husk h = ((Husk) zombie);
                h.setArmsRaised(true);
                h.setCanBreakDoors(false);
                h.setCanPickupItems(false);
                h.setTarget(PlayerParser.getNearestPlayer(h));
            }
            case DROWNED -> {
                Drowned d = ((Drowned) zombie);
                d.setArmsRaised(true);
                d.setCanBreakDoors(false);
                d.setCanPickupItems(false);
                d.setTarget(PlayerParser.getNearestPlayer(d));
            }
            case ZOMBIE_VILLAGER -> {
                ZombieVillager v = ((ZombieVillager) zombie);
                v.setArmsRaised(true);
                v.setCanBreakDoors(false);
                v.setCanPickupItems(false);
                v.setTarget(PlayerParser.getNearestPlayer(v));
            } default -> throw new IllegalStateException("올바르지 않은 좀비가 좀비 타입에 대입되었습니다");
        }
    }
}
