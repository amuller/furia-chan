package org.kit.furia.fragment.x86;

/*
 OBSearch: a distributed similarity search engine This project is to
 similarity search what 'bit-torrent' is to downloads. 
 Copyright (C) 2008 Arnoldo Jose Muller Molina

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * X86Instruction
 * @author Arnoldo Jose Muller Molina
 */

public class X86Instruction implements Comparable{
    

    /**
     * Address of the instruction.
     */
    private long address;
    
    private String instruction;
    
    private Param[] params;
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        X86Instruction other = (X86Instruction)o;
        if(address > other.address){
            return 1;
        }else if (address < other.address){
            return -1;
        }else{
            return 0;
        }
        
    }
}
