package net.buddat.wplanner;

import appinstance.AppLock;

/**
 * Hello world!
 *
 */
public class WPlanner 
{
    public static void main( String[] args )
    {
        AppLock.setLock("WPlanner");
        System.out.println( "Hello World!" );
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
