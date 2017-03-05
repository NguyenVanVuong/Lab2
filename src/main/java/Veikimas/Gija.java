package Veikimas;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dell
 */
import java.util.*;
import java.io.File;
public class Gija extends Thread {
    
    
    public void run(File file,  Map<String,Integer> queryCounts) {
        try
        {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext())
            {
                String word = scanner.next().toLowerCase();
                if (queryCounts.get(word) != null)
                {
                    queryCounts.put(word, queryCounts.get(word) + 1);
                }
                else queryCounts.put(word, 1);
            }
        }
        catch (Exception e)
        {
            
        
        }
    } 
}
