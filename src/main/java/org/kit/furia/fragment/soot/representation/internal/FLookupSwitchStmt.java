/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package org.kit.furia.fragment.soot.representation.internal;

import soot.*;
import org.kit.furia.fragment.soot.representation.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import java.util.*;

public class FLookupSwitchStmt extends JLookupSwitchStmt implements Qable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1519655698102620353L;

	// This method is necessary to deal with constructor-must-be-first-ism.
    private static UnitBox[] getTargetBoxesArray(List targets)
    {
        UnitBox[] targetBoxes = new UnitBox[targets.size()];

        for(int i = 0; i < targetBoxes.length; i++)
            targetBoxes[i] = Frimp.v().newStmtBox((Stmt) targets.get(i));

        return targetBoxes;
    }

    public FLookupSwitchStmt(Value key, List lookupValues, List targets, Unit defaultTarget)
    {
        super(Frimp.v().newExprBox(key),
              lookupValues, getTargetBoxesArray(targets),
              Frimp.v().newStmtBox(defaultTarget));
    }


    public Object clone()  // NOPMD by amuller on 11/16/06 4:12 PM
    {
        int lookupValueCount = getLookupValues().size();
        List clonedLookupValues = new ArrayList(lookupValueCount);

        for( int i = 0; i < lookupValueCount ;i++) {
            clonedLookupValues.add(i, Integer.valueOf(getLookupValue(i)));
        }
        
        return new FLookupSwitchStmt(Frimp.cloneIfNecessary(getKey()), clonedLookupValues, getTargets(), getDefaultTarget());
    }
    
    public String toQ() {
	
    	assert false : "This method must not be called, we already took care of case expressions";
		return null;
	}

}
