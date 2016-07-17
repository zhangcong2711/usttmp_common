/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.util;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author zhangcong
 */
public class SetUtil {
    
    //return union of two collections
    public static Set union(Set collection1, Set collection2) {
        Set set = new CopyOnWriteArraySet(collection1);
        set.addAll(collection2);
        return set;
    }

    //return intersection of two collections
    public static Set intersection(Set collection1, Set collection2) {
        Set set = new CopyOnWriteArraySet(collection1);
        set.retainAll(collection2);
        return set;
    }

    //return set difference of two collections
    public static Set setDifference(Set collection1, Set collection2) {
        Set set = new CopyOnWriteArraySet(collection1);
        set.removeAll(collection2);
        return set;
    }
    
}
