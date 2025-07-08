import com.sun.source.tree.LambdaExpressionTree;

import java.awt.print.Paper;
import java.util.*;
import java.util.spi.CalendarDataProvider;

enum BookType{
    PAPER, EBOOK, SHOWCASE;
}
abstract class Book{
    protected String ISBN;
    protected String title;
    protected String author;
    protected int year;
    protected double price;

    public Book(String ISBN, String title, String author, int year, double price){
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }

    // A method to get the book type using the above enum
    public abstract BookType getType();
    public abstract boolean isPurchasable();
    public abstract boolean canFullfillOrder(int quantity);
    public abstract double processPurchase(int quantity, String email, String address) throws Exception;

    public String getISBN(){return this.ISBN;}
    public String getTitle(){return this.title;}
    public String getAuthor(){return this.author;}
    public int getYear(){return this.year;}
    public double getPrice(){return this.price;}

    @Override
    public String toString(){
        return this.title + " by " + this.author + " (" + this.year + ") - ISBN: " + this.ISBN;
    }
}
class PaperBook extends Book{
    private int stock;

    public PaperBook(String ISBN, String title, String author, int year, double price, int stock){
        super(ISBN, title, author, year, price);
        this.stock = stock;
    }

    @Override
    public BookType getType(){
        return BookType.PAPER;
    }
    @Override
    public boolean isPurchasable(){
        return true;
    }
    @Override
    public boolean canFullfillOrder(int quantity){
        return stock >= quantity;
    }
    @Override
    public double processPurchase(int quantity, String email, String address) throws Exception{
        if(!canFullfillOrder(quantity)){
            throw new Exception("Insufficient stock to cover the required quantity");
        }
        stock -= quantity;
        double totalAmount = price * quantity;

        shippingService.shipBook(this, quantity, address);

        return totalAmount;
    }

    public int getStock(){
        return this.stock;
    }
    public void setStock(int stock){
        this.stock = stock;
    }
}

class EBook extends Book{
    private String fileType;

    public EBook(String ISBN, String title, String author, int year, double price, String fileType){
        super(ISBN, title, author, year, price);
        this.fileType = fileType;
    }

    @Override
    public BookType getType(){
        return BookType.EBOOK;
    }
    @Override
    public boolean isPurchasable(){
        return true;
    }
    @Override
    public boolean canFullfillOrder(int quantity){
        return true;
    }
    @Override
    public double processPurchase(int quantity, String email, String address) throws Exception{
        double totalAmount = price * quantity;
        MailService.sendEbook(this, quantity, email);

        return totalAmount;
    }

    public String getFileType(){
        return this.fileType;
    }
}

class showCaseBook extends Book{

    public showCaseBook(String ISBN, String title, String author, int year, double price){
        super(ISBN, title, author, year, 0.0);
    }

    @Override
    public BookType getType(){
        return BookType.SHOWCASE;
    }
    @Override
    public boolean isPurchasable(){
        return false;
    }
    @Override
    public boolean canFullfillOrder(int quantity){
        return false;
    }
    @Override
    public double processPurchase(int quantity, String email, String address) throws Exception{
        throw new Exception("Showcase books are not for sale!");
    }
}

class shippingService{
    public static void shipBook(PaperBook book, int quantity, String address){
        System.out.println("Shipping Details: " + quantity + " copy(ies) of '" +
                book.getTitle() + "' to " + address);
    }
}

class MailService{
    public static void sendEbook(EBook book, int quantity, String email){
        System.out.println("Shipping Details: " + quantity + " copy(ies) of '" +
                book.getTitle() + "' (" + book.getFileType() + ") to " + email);
    }
}

class QuantumStoreInventory{
    private Map<String, Book> inventory;

    public QuantumStoreInventory(){
        this.inventory = new HashMap<>();
    }

    public void addBook(Book book){
        inventory.put(book.getISBN(), book);
        System.out.println("QuantumStore: Added Book - " + book);
    }

    public void removeOutdatedBooks(int yearThreshold){
        int currentYear = 2025;
        for(Book instance : inventory.values()){
            if(instance.getYear() - currentYear > yearThreshold){
                inventory.remove(instance.getISBN());
                System.out.println("Quantum book store: Removed outdated book - " + instance);
            }
        }
    }

    public double buyBook(String ISBN, int quantity, String email, String address) throws Exception{
        Book book = inventory.get(ISBN);

        if(book == null){
            throw new Exception("Book with " + ISBN + " Not Found!");
        }
        if(!book.isPurchasable()){
            throw new Exception("Book '" + book.getTitle() + "' is not for sale");
        }
        double totalAmount = book.processPurchase(quantity, email, address);
        System.out.println("Quantum Bookstore: Purchase Complete. Total amount: " + totalAmount);

        return totalAmount;

    }
    public Book getBook(String ISBN){
        return inventory.get(ISBN);
    }

    public List<Book> getBooksByType(BookType type){
        List<Book> list = new ArrayList<>();
        for(Book instance : inventory.values()){
            if(instance.getType() == type){
                list.add(instance);
            }
        }
        return list;
    }

    public List<Book> getAllBooks(){
        return new ArrayList<>(inventory.values());
    }
    public void displayInventory(){
        System.out.println("Quantum Bookstore: Current Inventory");
        if(inventory.isEmpty()) {
            System.out.println("No books available");
        }
        else{
            for(Book instance : inventory.values()){
                System.out.println(instance);
            }
        }
    }
}

class QuantumBookstoreTesting {
    public static void main(String[] args) {

    }
}