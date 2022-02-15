package com.lmaniya.loocall.fetch_shop.fetch_product.customer;

public class cart_data {
    String Product_title;
    String image;
    public static String cart_id;
    String product_quantity;
    String Attribute;

    public String getProduct_title() {
        return Product_title;
    }

    public void setProduct_title(String product_title) {
        Product_title = product_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public int getTotal_price() {
        return Total_price;
    }

    public void setTotal_price(int total_price) {
        Total_price = total_price;
    }

    String product_price;
    int Total_price;


    public int Total_price() {
       return  getTotal_price();
    }
}
