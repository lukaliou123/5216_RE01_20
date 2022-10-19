package comp5216.sydney.edu.au.afinal.entity;

import java.util.ArrayList;

public class Events {

    ArrayList<EventEntity> list;

    private volatile static Events singleton;
    private Events (){
        list = new ArrayList<>();
    }

    public static Events getSingleton() {
        if (singleton == null) {
            synchronized (Events.class) {
                if (singleton == null) {
                    singleton = new Events();
                }
            }
        }
        return singleton;
    }

    public void add(EventEntity event){
        list.clear();
        list.add(event);
    }

    public EventEntity get(){
        return list.get(0);
    }

}
