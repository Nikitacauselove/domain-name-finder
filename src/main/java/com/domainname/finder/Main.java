package com.domainname.finder;

import com.domainname.finder.controller.DomainNameController;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin.create()
                .get("/domain", context -> {
                    String ip = context.queryParamAsClass("ip", String.class).get();
                    Integer thread = context.queryParamAsClass("thread", Integer.class).get();

                    DomainNameController.findAll(ip, thread);
                })
                .start(7070);
    }
}