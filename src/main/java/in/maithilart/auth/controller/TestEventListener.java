package in.maithilart.auth.controller;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

import in.maithilart.common.dto.MaithilEventMessage;
@Component
public class TestEventListener {

    @EventListener
    public void handle(MaithilEventMessage event) {

        System.out.println("================EVENT RECEIVED=======================");
        System.out.println(event);
    }
}