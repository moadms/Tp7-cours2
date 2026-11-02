package ma.rest.spring.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.rest.spring.entities.Compte;
import ma.rest.spring.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Path("/banque")
public class CompteRestJaxRSAPI {

    @Autowired
    private CompteRepository compteRepository;

    // READ: Récupérer tous les comptes (JSON et XML)
    @Path("/comptes")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Compte> getComptes() {
        return compteRepository.findAll();
    }

    // READ: Récupérer un compte par son identifiant (JSON et XML)
    @Path("/comptes/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCompte(@PathParam("id") Long id) {
        return compteRepository.findById(id)
                .map(compte -> Response.ok(compte).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // CREATE: Ajouter un nouveau compte (JSON et XML)
    @Path("/comptes")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addCompte(Compte compte) {
        Compte saved = compteRepository.save(compte);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    // UPDATE: Mettre à jour un compte existant (JSON et XML)
    @Path("/comptes/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCompte(@PathParam("id") Long id, Compte compte) {
        return compteRepository.findById(id)
                .map(existingCompte -> {
                    existingCompte.setSolde(compte.getSolde());
                    existingCompte.setDateCreation(compte.getDateCreation());
                    existingCompte.setType(compte.getType());
                    Compte updated = compteRepository.save(existingCompte);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // DELETE: Supprimer un compte (JSON et XML)
    @Path("/comptes/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteCompte(@PathParam("id") Long id) {
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
