/*****************************************************************************

JEP - Java Math Expression Parser 2.3.1
      January 26 2006
      (c) Copyright 2004, Nathan Funk and Richard Morris
      See LICENSE.txt for license information.

*****************************************************************************/
package org.nfunk.jep.function;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Complex;

/**
 * The acos function.
 * @author Nathan Funk
 * TODO How to handle acos(x) for real x with x>1 or x<-1
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArcCosine extends PostfixMathCommand
{
	public ArcCosine()
	{
		numberOfParameters = 1;
	
	}
	
	public Object acos(Object param)
		throws ParseException
	{
		if (param instanceof Complex)
		{
			return ((Complex)param).acos();
		}
		else if (param instanceof Number)
		{
			return new Double(Math.acos(((Number)param).doubleValue()));
		}

		throw new ParseException("Invalid parameter type");
	}

	@Override
	public void run(Stack inStack)
		throws ParseException 
	{
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(acos(param));//push the result on the inStack
		return;
	}
	
}
