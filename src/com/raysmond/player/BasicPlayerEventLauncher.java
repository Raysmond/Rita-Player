/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raysmond.player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 本类是一个线�?�?��永远在分派事件的线程
 * 它里面维护着�?��链表
 * @author hadeslee
 */
public class BasicPlayerEventLauncher extends Thread {

    private Set<BasicPlayerListener> listeners;
    private LinkedList<BasicPlayerEvent> queue;   
    /**
     * 默认的构造函�?只是包内友好,
     * 别的包不能初始化本类,因为本类只是给BasicPlayer�?��用的
     */
     BasicPlayerEventLauncher() {
        super("BasicPlayerEvent Dispacther Thread");
        listeners = new HashSet<BasicPlayerListener>();
        queue = new LinkedList<BasicPlayerEvent>();
    }

    /**
     * 把事件加进去
     * @param event 事件
     */
    public synchronized void put(BasicPlayerEvent event) {
        queue.offer(event);
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * 添加事件监听�?     * @param listener 监听�?     */
    public synchronized void addBasicPlayerListener(BasicPlayerListener listener) {
        listeners.add(listener);
    }

    /**
     * 得到�?��监听器的集合
     * @return �?��监听�?     */
    public synchronized Set<BasicPlayerListener> getBasicPlayerListeners() {
        return listeners;
    }

    /**
     * 去除指定的监听器
     * @param listener 要去除的临听�?     */
    public synchronized void removeBasicPlayerListener(BasicPlayerListener listener) {
        listeners.remove(listener);
    }

    public void run() {
        while (true) {
            BasicPlayerEvent event = queue.poll();
            if (event == null) {             
            	synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BasicPlayerEventLauncher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {              
            	for (BasicPlayerListener bpl : listeners) {
                    bpl.stateUpdated(event);
                }
            }
        }

    }
}
