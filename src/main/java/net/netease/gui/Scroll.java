package net.netease.gui;


import lombok.Setter;
import net.netease.utils.ContinualAnimation;
import org.lwjgl.input.Mouse;

public class Scroll {
   @Setter
   private float target;
   @Setter
   private float maxTarget;
   private final ContinualAnimation scrollAnim = new ContinualAnimation();

   public void use() {
      int wheel = Mouse.getDWheel();
      if (wheel != 0) {
         this.target = wheel > 0 ? (this.target += 15.0f) : (this.target -= 15.0f);
      }
      this.target = -Math.max(0.0f, Math.min(-this.target, this.maxTarget));
   }

   public void animate() {
      this.scrollAnim.animate(this.target, 22);
   }

   public float getAnimationTarget() {
      return this.scrollAnim.getOutput();
   }

   public float getTarget() {
      return this.target;
   }

   public float getMaxTarget() {
      return this.maxTarget;
   }

   public ContinualAnimation getScrollAnim() {
      return this.scrollAnim;
   }

}
