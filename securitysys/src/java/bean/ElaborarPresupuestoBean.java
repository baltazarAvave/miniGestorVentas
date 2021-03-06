package bean;

import entities.PresupuestoCab;
import entities.PresupuestoCabPK;
import entities.PresupuestoDet;
import entities.PresupuestoDetPK;
import entities.Productos;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Data;
import org.primefaces.event.RowEditEvent;
import session.util.JsfUtil;

/**
 *
 * @author sebas
 */
@ManagedBean(name = "ElaborarPresupuestoBean")
@ViewScoped
@Data
public class ElaborarPresupuestoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nroDeOrden;
    private String proveedor; //CNDDI = Codigo No Dependiente Del Idioma
    private String language; //{ESN,ENU,FRA}
    private String orderBy; //Pedido
    private BigDecimal pedido;
    private boolean active;
    private boolean translate;
    private boolean multilingual;
    private String estado;
    private String description;
    private boolean editando = false;
    private String fechaPedido;
    private String fechaRecepcion;
    private ArrayList<PresupuestoDet> listaDetalle = new ArrayList<PresupuestoDet>();
    private ArrayList<PresupuestoDet> listaDetallesEliminados = new ArrayList<PresupuestoDet>();
    private List<Productos> listaProductos = new ArrayList<Productos>();
    private String sumaTotal;
    private String iva;
    private String presupuestoTotal;
    private String cliente;
    private int idCliente;
    private String nroDocumento;
    private String razonsocial;
    private String ciudad;
    private String direccion;
    private String telefono;

    private PresupuestoCab presupuestoCab;
    private PresupuestoDet presupuestoDet;

    @EJB
    private bean.ProductosFacade productoFacade = new ProductosFacade();

    @EJB
    private bean.PresupuestoCabFacade presupuestoCabFacade = new PresupuestoCabFacade();

    @EJB
    private bean.PresupuestoDetFacade presupuestoDetFacade = new PresupuestoDetFacade();

    @PostConstruct
    void initialiseSession() {
        this.cargarVista();
    }

    public void cargarVista() {

        try {

            nroDeOrden = "0001";

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);
            fechaPedido = today;

            //Crea un presupuestoCab inicial
            this.presupuestoCab = new PresupuestoCab();
            
            PresupuestoCabPK presupuestoCabPK = new PresupuestoCabPK();
            presupuestoCabPK.setNroPresupuesto(BigInteger.ZERO);
            
            this.presupuestoCab.setPresupuestoCabPK(presupuestoCabPK);
            
            this.presupuestoCab.setFecha(date);
            

            this.listaProductos = productoFacade.findAll();

            proveedor = ""; //CNDDI = Codigo No Dependiente Del Idioma
            language = "ESN"; //{ESN,ENU,FRA} por defecto Español
            orderBy = ""; //Pedido
            pedido = null;
            active = false;
            translate = false;
            multilingual = false;
            estado = "Pendiente";
            description = "";

        } catch (Exception ex) {
        }
    }

    public String guardarPresupuesto() {

        presupuestoCab = new PresupuestoCab();

        presupuestoCab.setBaseImponible(new BigInteger(this.sumaTotal));

        persistPresupuestoCab(JsfUtil.PersistAction.CREATE, null);
        System.out.println("se guardó la PresupuestoCab con exito > " + JsfUtil.isValidationFailed());

        persistPresupuestoDet(JsfUtil.PersistAction.CREATE, listaDetalle, "Orden de Trabajo guardada correctamente");
        System.out.println("se guardó la PresupuestoDet con exito");

        //limpiar campos
        cargarVista();

        return null;
    }

    private void persistPresupuestoCab(JsfUtil.PersistAction persistAction, String successMessage) {
        if (presupuestoCab != null) {

            try {
                if (null != persistAction) {
                    switch (persistAction) {
                        case CREATE:
                            presupuestoCabFacade.create(presupuestoCab);
                            break;
                        case UPDATE:
                            presupuestoCabFacade.edit(presupuestoCab);
                            break;
                        default:
                            presupuestoCabFacade.remove(presupuestoCab);
                            break;
                    }
                }

                if (successMessage != null) {
                    JsfUtil.addSuccessMessage(successMessage);
                }

            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    private void persistPresupuestoDet(JsfUtil.PersistAction persistAction,
            ArrayList<PresupuestoDet> listaDetalle,
            String successMessage) {

        if (!listaDetallesEliminados.isEmpty()) { //si eliminé alguno de los detalles
            for (PresupuestoDet presDet : listaDetallesEliminados) {
                try {
                    presupuestoDetFacade.remove(presDet);
                } catch (EJBException ex) {
                    String msg = "";
                    Throwable cause = ex.getCause();
                    if (cause != null) {
                        msg = cause.getLocalizedMessage();
                    }
                    if (msg.length() > 0) {
                        JsfUtil.addErrorMessage(msg);
                    } else {
                        JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            }
        }

        if (listaDetalle != null && !listaDetalle.isEmpty()) {
            for (PresupuestoDet preDet : listaDetalle) {

                if (preDet.getPresupuestoCab() != null) {
                    presupuestoDet = preDet; //si mantiene el nro de orden, es que solo edite la descripción
                } else {
                    presupuestoDet = new PresupuestoDet();
                    presupuestoDet.setPresupuestoCab(presupuestoCab);
                    presupuestoDet.setCantidad(preDet.getCantidad());
                    presupuestoDet.setPrecio(preDet.getPrecio());
                    presupuestoDet.setTotalDescuento(preDet.getTotalDescuento());
                    presupuestoDet.setTotalDetalle(preDet.getTotalDetalle());
                    presupuestoDet.setCodProducto(preDet.getCodProducto());

                }

                try {
                    if (null != persistAction) {
                        switch (persistAction) {
                            case CREATE:
                                presupuestoDetFacade.create(presupuestoDet);
                                break;
                            case UPDATE:
                                presupuestoDetFacade.edit(presupuestoDet);
                                break;
                            default:
                                presupuestoDetFacade.remove(presupuestoDet);
                                break;
                        }
                    }

                } catch (EJBException ex) {
                    String msg = "";
                    Throwable cause = ex.getCause();
                    if (cause != null) {
                        msg = cause.getLocalizedMessage();
                    }
                    if (msg.length() > 0) {
                        JsfUtil.addErrorMessage(msg);
                    } else {
                        JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            }

            if (successMessage != null) {
                JsfUtil.addSuccessMessage(successMessage);
            }
        }
    }

    public void refrescarFooter() {
        System.out.println("Suma: " + getSumaTotal());
        System.out.println("IVA: " + getIva());
        System.out.println("Total: " + getPresupuestoTotal());
    }

    public String getSumaTotal() {
        int total = 0;
        for (PresupuestoDet det : listaDetalle) { // this is the list used in the value attribute of datatable
            total += det.getTotalDetalle().intValue();
        }
        this.sumaTotal = new DecimalFormat("###,###").format(total);
        return this.sumaTotal;
    }

    public String getIva() {
        int ivaCalculado = 0;
        int total = 0;
        for (PresupuestoDet det : listaDetalle) { // this is the list used in the value attribute of datatable
            total += det.getTotalDetalle().intValue();
        }

        ivaCalculado = total * 10 / 100;

        this.iva = new DecimalFormat("###,###").format(ivaCalculado);
        return this.iva;
    }

    public String getPresupuestoTotal() {
        int ivaCalculado = 0;
        int total = 0;
        for (PresupuestoDet det : listaDetalle) { // this is the list used in the value attribute of datatable
            total += det.getTotalDetalle().intValue();
        }

        ivaCalculado = total * 10 / 100;

        int presupuestoCalculado = total + ivaCalculado;

        this.presupuestoTotal = new DecimalFormat("###,###").format(presupuestoCalculado);
        return this.presupuestoTotal;
    }

    public void obtenerDatosCliente() {
        String nroDocumentoCliente = this.cliente;
        System.out.println("nroDocumentoCliente: " + nroDocumentoCliente);
        if (nroDocumentoCliente != null && !"".equalsIgnoreCase(nroDocumentoCliente)) {
            Connection con = null;
            PreparedStatement ps = null;

            try {
                con = DataConnect.getConnection();
                ps = con.prepareStatement("SELECT  cl.id_cliente, "
                        + "ci.ciudad, es.estado, td.tipo_docu, "
                        + "cl.nombre, cl.apellido, cl.direccion, "
                        + "cl.telefono, cl.nro_documento "
                        + "FROM cliente cl, ciudad ci, "
                        + "estado es, tipo_documento td "
                        + "where nro_documento = ? "
                        + "and ci.id_ciudad = cl.id_ciudad "
                        + "and es.id_estado = cl.id_estado "
                        + "and td.id_tipo_docu = cl.id_tipo_docu");
                ps.setString(1, nroDocumentoCliente);
                //ps.setInt(1, Integer.parseInt(nroDocumentoCliente));

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    this.idCliente = rs.getInt("id_cliente");
                    this.nroDocumento = rs.getString("nro_documento");
                    this.razonsocial = rs.getString("nombre") + " " + rs.getString("apellido");
                    this.ciudad = rs.getString("ciudad");
                    this.direccion = rs.getString("direccion");
                    this.telefono = rs.getString("telefono");
                }
            } catch (SQLException ex) {
                System.out.println("Error al obtener Cliente -->" + ex.getMessage());

            } finally {
                DataConnect.close(con);

            }

        }
    }

    public void onRowEditDetalle(RowEditEvent event) {
        PresupuestoDet det = (PresupuestoDet) event.getObject();
        BigInteger precio = det.getPrecio();
        BigInteger cantidad = det.getCantidad();
        BigInteger descuento = det.getTotalDescuento();
        int total = (precio.intValue() * cantidad.intValue()) - descuento.intValue();
        det.setTotalDetalle(new BigInteger(total + ""));
        //this.sumaTotal = refreshOrdenCompraTotal();
        System.out.println("Editando Producto: " + det.getCodProducto().getDescripcion() + ", total a pagar: " + det.getTotalDetalle());
    }

    public void onRowCancelDetalle(RowEditEvent event) {
        PresupuestoDet det = (PresupuestoDet) event.getObject();

        String descripcion = det.getCodProducto().getDescripcion(); //obtengo la descripción de la tarea

        //si es vacio, es porque es nuevo
        if ("".equalsIgnoreCase(descripcion)) {
            //al cancelar borro la ultima fila insertada
            listaDetalle.remove(det);
        }
    }

    public void addTarea() {
        int i = this.listaDetalle.size() + 1;
        PresupuestoDetPK detPK = new PresupuestoDetPK();
        detPK.setNroSecuencia(BigInteger.valueOf(i));
        PresupuestoDet predet = new PresupuestoDet(detPK);
        predet.setCodProducto(new Productos());
        predet.setTotalDescuento(BigInteger.ZERO);
        predet.setTotalDetalle(BigInteger.ZERO);
        this.listaDetalle.add(predet);
    }

    public void removeTarea(PresupuestoDet item) {
        listaDetallesEliminados.add(item);
        this.listaDetalle.remove(item);
    }

}
