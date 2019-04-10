package delivery.spaxsoftware.instamall.interfaces;

import android.view.View;

import delivery.spaxsoftware.instamall.entities.product.Product;

public interface RelatedProductsRecyclerInterface {

    void onRelatedProductSelected(View v, int position, Product product);
}
