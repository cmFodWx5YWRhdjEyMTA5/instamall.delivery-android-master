package delivery.spaxsoftware.spaxshop.interfaces;

import android.view.View;

import delivery.spaxsoftware.spaxshop.entities.product.Product;

public interface RelatedProductsRecyclerInterface {

    void onRelatedProductSelected(View v, int position, Product product);
}
