package cc.minetale.commonlib.modules.pigeon.payloads.minecraft;

import cc.minetale.commonlib.modules.pigeon.enums.MessagePlayerType;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

@Getter @Payload
public class MessagePlayerPayload extends BasePayload {

    @Transmit MessagePlayerType type;
    @Transmit UUID player;
    @Transmit String permission;
    @Transmit Component message;
    @Transmit List<Component> messageList;

    public MessagePlayerPayload() {
        payloadId = "messagePlayerPayload";
    }

    public MessagePlayerPayload(UUID player, Component message) {
        this();
        this.type = MessagePlayerType.PLAYER_COMPONENT;
        this.player = player;
        this.message = message;
    }

    public MessagePlayerPayload(UUID player, List<Component> messageList) {
        this();
        this.type = MessagePlayerType.PLAYER_COMPONENT_LIST;
        this.player = player;
        this.messageList = messageList;
    }

    public MessagePlayerPayload(String permission, Component message) {
        this();
        this.type = MessagePlayerType.PERMISSION_COMPONENT;
        this.permission = permission;
        this.message = message;
    }

    public MessagePlayerPayload(String permission, List<Component> messageList) {
        this();
        this.type = MessagePlayerType.PERMISSION_COMPONENT_LIST;
        this.permission = permission;
        this.messageList = messageList;
    }

    @Override
    public void receive() {}

}
