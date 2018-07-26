package delivery.spaxsoftware.spaxshop.interfaces;

import delivery.spaxsoftware.spaxshop.entities.cart.CartDiscountItem;
import delivery.spaxsoftware.spaxshop.entities.cart.CartProductItem;

public interface CartRecyclerInterface {

    void onProductUpdate(CartProductItem cartProductItem);

    void onProductDelete(CartProductItem cartProductItem);

    void onDiscountDelete(CartDiscountItem cartDiscountItem);

    void onProductSelect(long productId);

}
