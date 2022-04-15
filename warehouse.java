package warehouse;

/*
 * @author Aesha Shah, aks254 , aks254@scarletmail.rutgers.edu
 */ 
public class Warehouse {
    private Sector[] sectors;
    
    // Initializes every sector to an empty sector
    public Warehouse() {
        sectors = new Sector[10];

        for (int i = 0; i < 10; i++) {
            sectors[i] = new Sector();
        }
    }
    
    /**
     * Provided method, code the parts to add their behavior
     */
    public void addProduct(int id, String name, int stock, int day, int demand) {
        evictIfNeeded(id);
        addToEnd(id, name, stock, day, demand);
        fixHeap(id);
    }

    /**
     * Add a new product to the end of the correct sector
     * Requires proper use of the .add() method in the Sector class
     */
    private void addToEnd(int id, String name, int stock, int day, int demand) {
        // IMPLEMENT THIS METHOD
        Product item = new Product(id, name, stock, day, demand);
        sectors[id%10].add(item);
    }

    /**
     * Fix the heap structure of the sector, assuming the item was already added
     * Requires proper use of the .swim() and .getSize() methods in the Sector class
     */
    private void fixHeap(int id) {
        // IMPLEMENT THIS METHOD
        sectors[id%10].swim(sectors[id%10].getSize());
    }

    /**
     * Delete the least popular item in the correct sector, only if its size is 5 while maintaining heap
     * Requires proper use of the .swap(), .deleteLast(), and .sink() methods in the Sector class
     */
    private void evictIfNeeded(int id) {
       // IMPLEMENT THIS METHOD
       if (sectors[id%10].getSize()<5) return;
       sectors[id%10].swap(1, sectors[id%10].getSize());
       sectors[id%10].deleteLast();
       sectors[id%10].sink(1);
    }

    /**
     * Update the stock of some item by some amount
     * Requires proper use of the .getSize() and .get() methods in the Sector class
     * Requires proper use of the .updateStock() method in the Product class
     */
    public void restockProduct(int id, int amount) {
        // IMPLEMENT THIS METHOD
        int x = sectors[id%10].getSize();
        for (int traverse = 1; traverse<=x; traverse++){
            if (sectors[id%10].get(traverse).getId()==id){
                sectors[id%10].get(traverse).updateStock(amount);
            }
        }
    }
    
    /**
     * Delete some arbitrary product while maintaining the heap structure in O(logn)
     * Requires proper use of the .getSize(), .get(), .swap(), .deleteLast(), .sink() and/or .swim() methods
     * Requires proper use of the .getId() method from the Product class
     */
    public void deleteProduct(int id) {
        // IMPLEMENT THIS METHOD
        int size = sectors[id%10].getSize();
        for (int traverse = 1; traverse<=size; traverse++){
            if (sectors[id%10].get(traverse).getId()==id){
                sectors[id%10].swap(traverse, size);
                sectors[id%10].deleteLast();
                sectors[id%10].sink(traverse);
                break;
            }
        }
    }
    
    /**
     * Simulate a purchase order for some product
     * Requires proper use of the getSize(), sink(), get() methods in the Sector class
     * Requires proper use of the getId(), getStock(), setLastPurchaseDay(), updateStock(), updateDemand() methods
     */
    public void purchaseProduct(int id, int day, int amount) {
        // IMPLEMENT THIS METHOD
        int size = sectors[id%10].getSize();
        for (int i = 1; i<=size; i++){
            if (sectors[id%10].get(i).getId()==id){
                if (amount<=sectors[id%10].get(i).getStock()){
                    sectors[id%10].get(i).setLastPurchaseDay(day);
                    sectors[id%10].get(i).updateDemand(amount);
                    sectors[id%10].get(i).updateStock(amount*-1);
                    sectors[id%10].sink(i);
                }
            }
        }
    }

    public void betterAddProduct(int id, String name, int stock, int day, int demand) {
        // IMPLEMENT THIS METHOD
        Product item = new Product(id, name, stock, day, demand);
        int curr = id%10;
        do {
            if (sectors[curr].getSize()==5) {
                curr++;
                if (curr == 10) curr=0;
            }
            else{
                sectors[curr].add(item);
                sectors[curr].swim(sectors[curr].getSize());
                return;
            }
        } while (curr!=id%10);
        addProduct(id, name, stock, day, demand);
    }

    /*
     * Returns the string representation of the warehouse
     */
    public String toString() {
        String warehouseString = "[\n";

        for (int i = 0; i < 10; i++) {
            warehouseString += "\t" + sectors[i].toString() + "\n";
        }
        
        return warehouseString + "]";
    }

    public Sector[] getSectors () {
        return sectors;
    }
}
