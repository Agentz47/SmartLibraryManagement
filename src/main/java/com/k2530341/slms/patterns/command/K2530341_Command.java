package com.k2530341.slms.patterns.command;

/**
 * Command interface for the Command pattern.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public interface K2530341_Command {
    /**
     * Execute the command.
     * @return true if successful, false otherwise
     */
    boolean execute();
    
    /**
     * Undo the command.
     */
    void undo();
    
    /**
     * Get command description.
     * @return Command description
     */
    String getDescription();
}
