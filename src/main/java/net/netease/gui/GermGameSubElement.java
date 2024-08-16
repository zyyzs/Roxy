package net.netease.gui;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.packet.impl.Packet26;
import net.netease.utils.Animation;
import net.netease.utils.DecelerateAnimation;
import net.netease.utils.RippleAnimation;

@Setter
@Getter
public class GermGameSubElement {
   private final int A;
   private final String sid;
   private final String name;
   private final List<String> desc;
   private final Animation hoverAnim = new DecelerateAnimation(300, 1.0);
   private RippleAnimation animation;
   private Runnable runnable;

   public GermGameSubElement(int index, String sid, String name, List<String> desc) {
      this.A = index;
      this.sid = sid;
      this.name = name;
      this.desc = desc;
      this.animation = new RippleAnimation();
   }

   public void joinGame(String guiName) {
      HashMap<String, Object> data = new HashMap<String, Object>();
      data.put("entry", this.A);
      data.put("sid", this.sid);
      String json = GsonUtil.toJson(data);
      String message = new StringBuilder().insert(0, "GUI$").append(guiName).append("@").append("entry/").append(this.A).toString();
      PacketProcessor.INSTANCE.setLastGameElement(this);
      PacketProcessor.INSTANCE.sendPacket(new Packet26(message, json));
   }

   public int getIndex() {
      return this.A;
   }
}
