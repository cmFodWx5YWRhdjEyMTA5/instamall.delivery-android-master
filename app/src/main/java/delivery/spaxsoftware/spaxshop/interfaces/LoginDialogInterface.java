package delivery.spaxsoftware.spaxshop.interfaces;

import delivery.spaxsoftware.spaxshop.entities.User;

/**
 * Interface declaring methods for login dialog.
 */
public interface LoginDialogInterface {

    void successfulLoginOrRegistration(User user);

}
