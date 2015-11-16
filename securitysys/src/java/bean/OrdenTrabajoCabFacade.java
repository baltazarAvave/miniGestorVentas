/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import entities.OrdenTrabajoCab;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author expsee
 */
@Stateless
public class OrdenTrabajoCabFacade extends AbstractFacade<OrdenTrabajoCab> {
    @PersistenceContext(unitName = "securitysysPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdenTrabajoCabFacade() {
        super(OrdenTrabajoCab.class);
    }
    
    public OrdenTrabajoCab findByNroOrden(Integer nroOrden) {
        try {
            return (OrdenTrabajoCab) em.createNamedQuery("OrdenTrabajoCab.findByNroOrden").setParameter("nroOrden", nroOrden).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
}
