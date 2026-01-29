/*

  Author: Devang Singh
  Email: dsingh2024@my.fit.edu
  Course: CSE 2010
  Section: E3
  Description of this file: HW1, Linked List implementation of inventory managagement for products on an e-commerce platform.

 */
import java.io.File; // To path the given input files to scanner class
import java.util.Scanner; // To read the input files to test the cases

public class HW1 { // driver class for program

    private static class SellerNode { // node class for sller entry in LinkedList
        String seller; // name of seller
        double price; // price of product forom seller
        double shipping; // shipping cost price from seller
        int quantity; // no. of items being bough and shipped from seller
        SellerNode next; // points to next node in seller list

        // Constructor for SellerNode class
        SellerNode(String seller, double price, double shipping, int quantity) {
            this.seller = seller;
            this.price = price;
            this.shipping = shipping;
            this.quantity = quantity;
            this.next = null; // new node points to nothing
        }
        // To calculate the total cost of order
        double totalCost() {
            return price + shipping;
        }
    }
    /* linked list class that stores seller for product **/
    private static class SellerList {
        private SellerNode head; // head of the linked list

        SellerList() { // to make empty seller list
            head = null;
        }

        // Inserts seller into the list ir sorted order
        void addSeller(String seller, double price, double shipping, int quantity) {
            SellerNode newNode = new SellerNode(seller, price, shipping, quantity);
            double newTotalCost = newNode.totalCost();

            // in case of empty list or insert before current head
            if (head == null ||
                    newTotalCost < head.totalCost() ||
                    (Math.abs(newTotalCost - head.totalCost()) < 0.0001 &&
                            seller.compareTo(head.seller) < 0)) {

                newNode.next = head;
                head = newNode;
                return;
            }

            SellerNode current = head;

            // traverse linked list to find the correct insertion point
            while (current.next != null) {
                double nextCost = current.next.totalCost();

                // Insert before higher-cost or alphabetically larger seller
                if (newTotalCost < nextCost ||
                        (Math.abs(newTotalCost - nextCost) < 0.0001 &&
                                seller.compareTo(current.next.seller) < 0)) {

                    newNode.next = current.next;
                    current.next = newNode;
                    return;
                }
                current = current.next;
            }

            // Insert at end if no earliwr position could be found
            current.next = newNode;
        }

        // removes a seller by name from the list
        boolean removeSeller(String seller) {
            if (head == null) return false;

            // *Case*:seller is at the head
            if (head.seller.equals(seller)) {
                head = head.next;
                return true;
            }

            SellerNode current = head;

            // Traverse list to find a seller to remve
            while (current.next != null) {
                if (current.next.seller.equals(seller)) {
                    current.next = current.next.next;
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        // To increase the  quantity for a seller.
        int increaseInventory(String seller, int addQty) {
            SellerNode node = find(seller);
            if (node == null) return -1;
            node.quantity += addQty;
            return node.quantity;
        }

        // to process customer purchase request
        int customerPurchase(String seller, int buyQty) {
            SellerNode node = find(seller);
            if (node == null) return -1;
            if (node.quantity < buyQty) return -2;

            node.quantity -= buyQty;
            return node.quantity;
        }

        // Displaying  the seller list in fixed-width format
        void display() {
            System.out.printf("%10s%14s%14s%11s%n",
                    "seller", "productPrice", "shippingCost", "totalCost");
            SellerNode cur = head;
            while (cur != null) {
                System.out.printf("%10s%14.2f%14.2f%11.2f%n",
                        cur.seller, cur.price, cur.shipping, cur.totalCost());
                cur = cur.next;
            }
        }

        // Searches for a seller node by name
        SellerNode find(String seller) {
            SellerNode cur = head;

            // To traverse the list until seller is found or list ends
            while (cur != null) {
                if (cur.seller.equals(seller)) return cur;
                cur = cur.next;
            }
            return null;
        }
    }

    // Actual entrey point for the program
    public static void main(String[] args) throws Exception {
        if (args.length < 1) return;

        SellerList appleIPhoneList = new SellerList();
        SellerList earBudsList = new SellerList();
        SellerList keyboardList = new SellerList();

        Scanner scanner = new Scanner(new File(args[0]));

        // Processes each cmd from input file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tok = line.split("\\s+");
            String cmd = tok[0];
            String product = tok[1];

            SellerList list = getList(product, appleIPhoneList, earBudsList, keyboardList);
            if (list == null) continue;

            // to handle the AddSeller command
            if (cmd.equals("AddSeller")) {
                String seller = tok[2];
                double price = Double.parseDouble(tok[3]);
                double shipping = Double.parseDouble(tok[4]);
                int qty = Integer.parseInt(tok[5]);

                if (qty <= 0) {
                    System.out.printf("AddSeller %s %s %.2f %.2f %d NonPositiveQuantityError%n",
                            product, seller, price, shipping, qty);
                } else {
                    System.out.printf("AddSeller %s %s %.2f %.2f %d%n",
                            product, seller, price, shipping, qty);
                    list.addSeller(seller, price, shipping, qty);
                }
            }

            // to handle Remove Seller command
            else if (cmd.equals("RemoveSeller")) {
                String seller = tok[2];
                if (list.removeSeller(seller)) {
                    System.out.printf("RemoveSeller %s %s%n", product, seller);
                } else {
                    System.out.printf("RemoveSeller %s %s NonExistingSellerError%n", product, seller);
                }
            }

            // for jandling IncreaseInventory command
            else if (cmd.equals("IncreaseInventory")) {
                String seller = tok[2];
                int qty = Integer.parseInt(tok[3]);
                int updated = list.increaseInventory(seller, qty);

                System.out.printf("IncreaseInventory %s %s %d %d%n",
                        product, seller, qty, updated);
            }

            // handle customer purchase command
            else if (cmd.equals("CustomerPurchase")) {
                String seller = tok[2];
                int qty = Integer.parseInt(tok[3]);
                int result = list.customerPurchase(seller, qty);

                if (result == -1 || result == -2) {
                    System.out.printf("CustomerPurchase %s %s %d NotEnoughInventoryError%n",
                            product, seller, qty);
                } else {
                    System.out.printf("CustomerPurchase %s %s %d %d%n",
                            product, seller, qty, result);

                    // To remove the seller when inventory is depleted
                    if (result == 0) {
                        System.out.printf("DepletedInventoryRemoveSeller %s %s%n",
                                product, seller);
                        list.removeSeller(seller);
                    }
                }
            }

            // Handle the DisplaySeller List command
            else if (cmd.equals("DisplaySellerList")) {
                System.out.printf("DisplaySellerList %s%n", product);
                list.display();
            }
        }
        scanner.close();
    }

    // Returns the seller list for a given product
    private static SellerList getList(String product,
                                      SellerList appleIPhoneList,
                                      SellerList earBudsList,
                                      SellerList keyboardList) {

        if (product.equals("appleIPhone")) return appleIPhoneList;
        if (product.equals("earBuds")) return earBudsList;
        if (product.equals("keyboard")) return keyboardList;
        return null;
    }
}