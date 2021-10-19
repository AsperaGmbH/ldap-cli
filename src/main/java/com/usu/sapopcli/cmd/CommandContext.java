package com.usu.sapopcli.cmd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CommandContext {

    private static Map<String, Class<? extends CommandRunnable>> commandMap = new HashMap<>();
    private static Queue<String> argumentStack = new LinkedList<>();
    private static CommandContext instance;

    public static synchronized CommandContext getInstance() {
        if (instance == null) {
            instance = new CommandContext();
            instance.loadCommands();
        }
        return instance;
    }

    private CommandContext() {
    }

    public void executeCommand(String command) throws CommandException {
        try {
            ((CommandRunnable) commandMap.get(command).getDeclaredConstructor().newInstance()).run();
        } catch (Exception e) {
            throw new CommandException(e.getMessage(), e);
        }
        clearArguments();
    }

    public void addCommand(String key, Class<? extends CommandRunnable> clazz) {
        commandMap.put(key, clazz);
    }

    public void removeCommand(String key) {
        commandMap.remove(key);
    }

    public boolean isCommand(String key) {
        return commandMap.containsKey(key);
    }

    public void addArgument(String arg) {
        argumentStack.add(arg);
    }

    public String nextArgument() {
        return argumentStack.poll();
    }

    public String[] allArguments() {
        return argumentStack.toArray(new String[0]);
    }

    public int sizeOfArguments() {
        return argumentStack.size();
    }

    public void clearArguments() {
        argumentStack.clear();
    }

    /**
     * Register commands on the CommandContext.
     */
    public void loadCommands() {
        addCommand("quit", QuitCommand.class);
        addCommand("q", QuitCommand.class);
        addCommand("h", HelpCommand.class);
        addCommand("help", HelpCommand.class);
        addCommand("ssl-ping", SSLPingCommand.class);
        addCommand("sp", SSLPingCommand.class);
        addCommand("lct", LdapConnectionTestCommand.class);
        addCommand("ldap-connection-test", LdapConnectionTestCommand.class);
        addCommand("lsc", LdapSearchCommand.class);
        addCommand("ldap-search-command", LdapSearchCommand.class);
    }
}
