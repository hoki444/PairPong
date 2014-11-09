package com.algy.schedcore;

import com.algy.schedcore.util.Lister;

class DogServer extends BaseCompServer {
    @Override
    public void hookAddComp(BaseComp comp) {
        System.out.println("HOOK " + comp);
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        System.out.println("UNHOOK " + comp);
        
    }

    @Override
    protected void onAdhered() {
        System.out.println("DogServer adhered");
    }

    @Override
    protected void onDetached() {
        System.out.println("DogServer detached");
        
    }

    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> compSigList) {
        compSigList.add(Dog.class);
    }
}

abstract class Dog extends BaseComp {
    public abstract void bark ();
}

class GoldenRetriever extends Dog {

    @Override
    public IComp duplicate() {
        return new GoldenRetriever();
    }

    @Override
    protected void onAdhered() {
        System.out.println("Hi Golden!");
    }

    @Override
    protected void onDetached() {
        System.out.println("Bye Golden!");
    }

    @Override
    public void bark() {
        System.out.println("Golden!Golden!");
    }

}
class Harrier extends Biggle {
    @Override
    protected void onDetached() {
        System.out.println("Bye Harrier!");
    }

    @Override
    protected void onAdhered() {
        System.out.println("Hi Harrier!");
    }

    @Override
    public void bark( ) {
        System.out.println("Harrier! Harrier!");
    }
}

class Biggle extends Dog {

    @Override
    public IComp duplicate() {
        return new Biggle();
    }

    @Override
    protected void onAdhered() {
        System.out.println("Hi Biggle!");
    }

    @Override
    protected void onDetached() {
        System.out.println("Bye Biggle!");
    }

    @Override
    public void bark() {
        System.out.println("Biggle!Biggle!");
    }
}

public class TestEntitySystem {
    public static void main(String ... args) {
        ICore core = new Core(ITickGetter.systemTickGetter);
        Item<BaseComp, ICore> item = Item.MakeCompItem();
        item.add(new GoldenRetriever());
        item.add(new Harrier());
        
        core.addServer(new DogServer());
        core.addItem(item);
        core.removeItem(item);
        core.removeServer(BaseCompServer.class);
    }
}
