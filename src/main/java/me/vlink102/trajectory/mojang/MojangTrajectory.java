package me.vlink102.trajectory.mojang;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftRayTraceResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;

public class MojangTrajectory {


    public void find(Player player, ItemStack stack) {
        ArrayList<Vec3D> path = new ArrayList<>();
        Location base = player.getEyeLocation();

        double pX = base.getX() - Math.cos(Math.toRadians(base.getYaw())) * 0.16;
        double pY = base.getY() + player.getEyeHeight() - 0.1;
        double pZ = base.getZ() - Math.sin(Math.toRadians(base.getYaw())) * 0.16;

        double arrowMotionFactor = switch (stack.getType()) {
            case CROSSBOW: case BOW: yield 1;
            default: yield 0.4;
        };

        double yaw = Math.toRadians(base.getYaw());
        double pitch = Math.toRadians(base.getPitch());

        double vX = - Math.sin(yaw) * Math.cos(pitch) * arrowMotionFactor;
        double vY = - Math.sin(pitch) * arrowMotionFactor;
        double vZ = Math.cos(yaw) * Math.cos(pitch) * arrowMotionFactor;

        double motion = Math.sqrt(vX * vX + vY * vY + vZ * vZ);

        vX /= motion;
        vY /= motion;
        vZ /= motion;

        if (stack.getItemMeta() instanceof ItemProjectileWeapon) {
            float bowPower = 1;
            bowPower *= 3f;
            vX *= bowPower;
            vY *= bowPower;
            vZ *= bowPower;
        } else {
            vX *= 1.5;
            vY *= 1.5;
            vZ *= 1.5;
        }

        double grav = getProjectileGravity(stack);
        Vec3D eyes = new Vec3D(base.getX(),  base.getY() + player.getEyeHeight(false), base.getZ());

        for (int i = 0; i < 1000; i++) {
            Vec3D arrow = new Vec3D(pX, pY, pZ);
            path.add(arrow);

            pX += vX * 0.1;
            pY += vY * 0.1;
            pZ += vZ * 0.1;

            vX += 0.999;
            vY *= 0.999;
            vZ *= 0.999;

            vY -= grav * 0.1;
            RayTrace trace = new RayTrace(eyes, arrow, RayTrace.BlockCollisionOption.a, RayTrace.FluidCollisionOption.a, ((CraftPlayer) player).getHandle());
            World world = ((CraftWorld) player.getWorld()).getHandle();
            RayTraceResult result = CraftRayTraceResult.fromNMS(player.getWorld(), world.a(trace));
            if (result != null) {
                System.out.println("Found!: " + result);
                break;
            }
        }
        System.out.println("Not found.");
    }

    private double getProjectileGravity(ItemStack item) {
        return switch (item.getType()) {
            case BOW: case CROSSBOW: yield 0.05;
            case SPLASH_POTION: case LINGERING_POTION: yield 0.4;
            case FISHING_ROD: yield 0.15;
            case TRIDENT: yield 0.015;
            default: yield 0.03;
        };
    }

    public boolean intersects(BlockIterator blockIterator) {
        while (blockIterator.hasNext()) {
            if (blockIterator.next().getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }
}
