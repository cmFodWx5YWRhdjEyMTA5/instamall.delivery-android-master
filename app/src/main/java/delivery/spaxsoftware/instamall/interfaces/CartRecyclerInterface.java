package delivery.spaxsoftware.instamall.interfaces;

import delivery.spaxsoftware.instamall.entities.cart.CartDiscountItem;
import delivery.spaxsoftware.instamall.entities.cart.CartProductItem;

public interface CartRecyclerInterface {

    void onProductUpdate(CartProductItem cartProductItem);

    void onProductDelete(CartProductItem cartProductItem);

    void onDiscountDelete(CartDiscountItem cartDiscountItem);

    void onProductSelect(long productId);

}
