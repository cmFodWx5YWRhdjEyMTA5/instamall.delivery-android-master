package delivery.spaxsoftware.spaxshop.interfaces;

import android.view.View;

import delivery.spaxsoftware.spaxshop.entities.order.Order;

public interface OrdersRecyclerInterface {

    void onOrderSelected(View v, Order order);

}
