/**

  Author: Devang Singh
  Email: dsingh2024@my.fit.edu
  Course: CSE 2010
  Section: E3
  Description of this file: HW1, Linked list for 3 Amazon products with basic functionalities

 */


import java.io.File;
import java.util.Scanner;

public class HW1 {

    private static class SellerNode {
        String seller;
        double price;
        double shipping;
        int quantity;
        SellerNode next;

        SellerNode(String seller, double price, double shipping, int quantity) {
            this.seller = seller;
            this.price = price;
            this.shipping = shipping;
            this.quantity = quantity;
            this.next = null;
        }

        double totalCost() {
            return price + shipping;
        }
    }

    private static class SellerList {
        private SellerNode head;

        SellerList() {
            head = null;
        }

        void addSeller(String seller, double price, double shipping, int quantity) {
            SellerNode newNode = new SellerNode(seller, price, shipping, quantity);
            double newTotalCost = newNode.totalCost();


            if (head == null || newTotalCost < head.totalCost() ||
                    (Math.abs(newTotalCost - head.totalCost()) < 0.0001 && seller.compareTo(head.seller) < 0)) {
                newNode.next = head;
                head = newNode;
                return;
            }

            SellerNode current = head;
            while (current.next != null) {
                double nextTotalCost = current.next.totalCost();
                if (newTotalCost < nextTotalCost ||
                        (Math.abs(newTotalCost - nextTotalCost) < 0.0001 && seller.compareTo(current.next.seller) < 0)) {
                    newNode.next = current.next;
                    current.next = newNode;
                    return;
                }
                current = current.next;
            }
            current.next = newNode;
        }

        boolean removeSeller(String seller) {
            if (head == null) return false;
            if (head.seller.equals(seller)) {
                head = head.next;
                return true;
            }
            SellerNode current = head;
            while (current.next != null) {
                if (current.next.seller.equals(seller)) {
                    current.next = current.next.next;
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        int increaseInventory(String seller, int addQty) {
            SellerNode node = find(seller);
            if (node == null) return -1;
            node.quantity += addQty;
            return node.quantity;
        }

        int customerPurchase(String seller, int buyQty) {
            SellerNode node = find(seller);
            if (node == null) return -1;
            if (node.quantity < buyQty) return -2;
            node.quantity -= buyQty;
            return node.quantity;
        }

        void display() {

            System.out.printf("%10s %13s %13s %10s%n", "seller", "productPrice", "shippingCost", "totalCost");
            SellerNode cur = head;
            while (cur != null) {
                System.out.printf("%10s %13.2f %13.2f %10.2f%n", cur.seller, cur.price, cur.shipping, cur.totalCost());
                cur = cur.next;
            }
        }

        SellerNode find(String seller) {
            SellerNode cur = head;
            while (cur != null) {
                if (cur.seller.equals(seller)) return cur;
                cur = cur.next;
            }
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) return;

        SellerList appleIPhoneList = new SellerList();
        SellerList earBudsList = new SellerList();
        SellerList keyboardList = new SellerList();

        Scanner scanner = new Scanner(new File(args[0]));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tok = line.split("\\s+");
            String cmd = tok[0];
            String product = tok[1]; // Correctly scoped for the whole loop

            SellerList list = getList(product, appleIPhoneList, earBudsList, keyboardList);

            // Hardcoded protection against invalid product names
            if (list == null) continue;

            if (cmd.equals("AddSeller")) {
                String seller = tok[2];
                double price = Double.parseDouble(tok[3]);
                double shipping = Double.parseDouble(tok[4]);
                int qty = Integer.parseInt(tok[5]);

                if (qty <= 0) {
                    System.out.printf("AddSeller %s %s %.2f %.2f %d NonPositiveQuantityError%n", product, seller, price, shipping, qty);
                } else {
                    System.out.printf("AddSeller %s %s %.2f %.2f %d%n", product, seller, price, shipping, qty);
                    list.addSeller(seller, price, shipping, qty);
                }
            }
            else if (cmd.equals("RemoveSeller")) {
                String seller = tok[2];
                if (list.removeSeller(seller)) {
                    System.out.printf("RemoveSeller %s %s%n", product, seller);
                } else {
                    System.out.printf("RemoveSeller %s %s NonExistingSellerError%n", product, seller);
                }
            }
            else if (cmd.equals("IncreaseInventory")) {
                String seller = tok[2];
                int qty = Integer.parseInt(tok[3]);
                int updated = list.increaseInventory(seller, qty);

                System.out.printf("IncreaseInventory %s %s %d %d%n", product, seller, qty, updated);
            }
            else if (cmd.equals("CustomerPurchase")) {
                String seller = tok[2];
                int qty = Integer.parseInt(tok[3]);
                int result = list.customerPurchase(seller, qty);

                if (result == -2 || result == -1) {
                    System.out.printf("CustomerPurchase %s %s %d NotEnoughInventoryError%n", product, seller, qty);
                } else {
                    System.out.printf("CustomerPurchase %s %s %d %d%n", product, seller, qty, result);

                    if (result == 0) {
                        System.out.printf("DepletedInventoryRemoveSeller %s %s%n", product, seller);
                        list.removeSeller(seller);
                    }
                }
            }
            else if (cmd.equals("DisplaySellerList")) {
                System.out.printf("DisplaySellerList %s%n", product);
                list.display();
            }
        }
        scanner.close();
    }

    private static SellerList getList(String product, SellerList appleIPhoneList,
                                      SellerList earBudsList, SellerList keyboardList) {
        if (product.equals("appleIPhone")) return appleIPhoneList;
        if (product.equals("earBuds")) return earBudsList;
        if (product.equals("keyboard")) return keyboardList;
        return null;
    }
}
/**Description of each method, including parameters
          description of variables
          description of each block (around 5-10 lines) of instructions */