package com.usu.ldapcli.cmd;

public class QuitCommand implements CommandRunnable {

    @Override
    public void run() {
        System.out.println("quit. good bye!");
        System.exit(0);
    }
}
