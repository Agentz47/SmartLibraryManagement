package com.k2530341.slms.patterns.command;

import java.util.Stack;

/**
 * Command manager that maintains command history and supports undo.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_CommandManager {
    private final Stack<K2530341_Command> commandHistory = new Stack<>();
    private final int maxHistorySize;
    
    public K2530341_CommandManager(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }
    
    public K2530341_CommandManager() {
        this(50); // Default max history
    }
    
    /**
     * Execute a command and add to history.
     * @param command The command to execute
     * @return true if successful
     */
    public boolean executeCommand(K2530341_Command command) {
        boolean success = command.execute();
        
        if (success) {
            commandHistory.push(command);
            
            // Limit history size
            if (commandHistory.size() > maxHistorySize) {
                commandHistory.remove(0);
            }
        }
        
        return success;
    }
    
    /**
     * Undo the last command.
     * @return true if undo was successful
     */
    public boolean undoLastCommand() {
        if (commandHistory.isEmpty()) {
            return false;
        }
        
        K2530341_Command lastCommand = commandHistory.pop();
        lastCommand.undo();
        return true;
    }
    
    /**
     * Get the command history.
     * @return Command history description
     */
    public String getHistoryDescription() {
        StringBuilder sb = new StringBuilder();
        for (K2530341_Command cmd : commandHistory) {
            sb.append(cmd.getDescription()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Clear command history.
     */
    public void clearHistory() {
        commandHistory.clear();
    }
    
    /**
     * Get the size of command history.
     * @return History size
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
}
