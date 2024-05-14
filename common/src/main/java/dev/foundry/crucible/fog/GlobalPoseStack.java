package dev.foundry.crucible.fog;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.client.JomlPoseStack;
import dev.foundry.crucible.extension.PoseStackDuck;

public final class GlobalPoseStack {

    private static final ThreadLocal<PoseStack> INSTANCE = ThreadLocal.withInitial(PoseStack::new);
    private static final ThreadLocal<JomlPoseStack> JOML = ThreadLocal.withInitial(JomlPoseStack::new);

    private GlobalPoseStack() {
    }

    public static PoseStack get() {
        return ((PoseStackDuck)INSTANCE.get()).reset();
    }

    public static JomlPoseStack getJoml() {
        return JOML.get().reset();
    }
}
