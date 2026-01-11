import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("navigationController")
@RequestScoped
public class NavigationController implements Serializable {

    // Page d'accueil
    public String home() {
        return "/home?faces-redirect=true";
    }

    // Réservations
    public String reservations() {
        return "/reservations?faces-redirect=true";
    }

    // Trajets
    public String trajets() {
        return "/trajets?faces-redirect=true";
    }

    // Rapports
    public String rapports() {
        return "/reports?faces-redirect=true";
    }

    // À propos
    public String apropos() {
        return "/pages/a_propos?faces-redirect=true";
    }

    // Profil utilisateur
    public String profil() {
        return "/pages/profil?faces-redirect=true";
    }

    // Connexion
    public String connexion() {
        return "/connexion?faces-redirect=true";
    }

    // Déconnexion (optionnel)
    public String deconnexion() {
        return "/connexion?faces-redirect=true";
    }
}
