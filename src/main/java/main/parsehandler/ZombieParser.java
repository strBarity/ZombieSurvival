package main.parsehandler;

import main.Main;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ZombieParser {
    /**
     * 특정 엔티티가 좀비 종류인지 확인하는 메소드
     * @param entity 좀비인지 확인할 엔티티
     * @return entity가 좀비 종류(좀비, 허스크, 드라운드, 좀비 주민 등...)일 경우 true 반환, 아닐 경우 false 반환
     */
    public static boolean isZombie(@NotNull Entity entity) {
        return entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.HUSK) || entity.getType().equals(EntityType.DROWNED) || entity.getType().equals(EntityType.ZOMBIE_VILLAGER);
    }
    /**
     * 특정 엔티티가 좀비 종류인지 확인하는 메소드
     * @param entity 좀비인지 확인할 엔티티
     * @return entity가 좀비 종류(좀비, 허스크, 드라운드, 좀비 주민 등...)일 경우 true 반환, 아닐 경우 false 반환
     */
    public static boolean isZombie(@NotNull LivingEntity entity) {
        return entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.HUSK) || entity.getType().equals(EntityType.DROWNED) || entity.getType().equals(EntityType.ZOMBIE_VILLAGER);
    }

    /**
     * 특정 좀비를 게임에 특화된 엔티티로 바꾸는 메소드
     * (디스폰 비활성화, 포션 이펙트 설정, 타겟 설정 등)
     * @param zombie 커스텀 좀비로 만들 좀비
     * @throws IllegalStateException zombie가 좀비가 아닐 경우
     */
    public static void asCustomZombie(@NotNull LivingEntity zombie) {
        try {
            if (!isZombie(zombie)) throw new IllegalStateException("올바르지 않은 좀비가 좀비 타입에 대입되었습니다");
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
                }
                case HUSK -> {
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
                }
                default -> throw new IllegalStateException("올바르지 않은 좀비가 좀비 타입에 대입되었습니다");
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
