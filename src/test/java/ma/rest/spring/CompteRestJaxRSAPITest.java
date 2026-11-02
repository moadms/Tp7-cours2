package ma.rest.spring;

import ma.rest.spring.entities.Compte;
import ma.rest.spring.entities.TypeCompte;
import ma.rest.spring.repositories.CompteRepository;
import ma.rest.spring.ws.CompteRestJaxRSAPI;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompteRestJaxRSAPITest {

    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private CompteRestJaxRSAPI compteRestJaxRSAPI;

    private Compte compte1;
    private Compte compte2;

    @BeforeEach
    void setUp() {
        compte1 = new Compte(1L, 4500.0, new Date(), TypeCompte.COURANT);
        compte2 = new Compte(2L, 8200.5, new Date(), TypeCompte.EPARGNE);
    }

    @Test
    @DisplayName("Test 1: Récupération de tous les comptes (GET /banque/comptes)")
    void testGetComptes() {
        when(compteRepository.findAll()).thenReturn(Arrays.asList(compte1, compte2));

        List<Compte> comptes = compteRestJaxRSAPI.getComptes();

        assertNotNull(comptes);
        assertEquals(2, comptes.size());
        assertEquals(4500.0, comptes.get(0).getSolde());
        verify(compteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 2: Récupération d'un compte par ID existant (GET /banque/comptes/1)")
    void testGetCompteByIdSuccess() {
        when(compteRepository.findById(1L)).thenReturn(Optional.of(compte1));

        Response response = compteRestJaxRSAPI.getCompte(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(compte1, response.getEntity());
        verify(compteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 3: Récupération d'un compte inexistant retourne 404 (GET /banque/comptes/99)")
    void testGetCompteByIdNotFound() {
        when(compteRepository.findById(99L)).thenReturn(Optional.empty());

        Response response = compteRestJaxRSAPI.getCompte(99L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Test 4: Création d'un nouveau compte (POST /banque/comptes)")
    void testAddCompte() {
        Compte nouveau = new Compte(null, 3200.0, new Date(), TypeCompte.COURANT);
        Compte saved = new Compte(3L, 3200.0, nouveau.getDateCreation(), TypeCompte.COURANT);
        when(compteRepository.save(any(Compte.class))).thenReturn(saved);

        Response response = compteRestJaxRSAPI.addCompte(nouveau);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(saved, response.getEntity());
    }

    @Test
    @DisplayName("Test 5: Mise à jour d'un compte existant (PUT /banque/comptes/1)")
    void testUpdateCompte() {
        Compte updatedInfo = new Compte(null, 5500.0, new Date(), TypeCompte.EPARGNE);
        when(compteRepository.findById(1L)).thenReturn(Optional.of(compte1));
        when(compteRepository.save(any(Compte.class))).thenReturn(compte1);

        Response response = compteRestJaxRSAPI.updateCompte(1L, updatedInfo);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(5500.0, compte1.getSolde());
        assertEquals(TypeCompte.EPARGNE, compte1.getType());
    }

    @Test
    @DisplayName("Test 6: Suppression d'un compte (DELETE /banque/comptes/1)")
    void testDeleteCompte() {
        when(compteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(compteRepository).deleteById(1L);

        Response response = compteRestJaxRSAPI.deleteCompte(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(compteRepository, times(1)).deleteById(1L);
    }
}
