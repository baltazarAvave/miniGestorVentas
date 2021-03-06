/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Acer
 */
@Entity
@Table(name = "cliente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByIdCliente", query = "SELECT c FROM Cliente c WHERE c.idCliente = :idCliente"),
    @NamedQuery(name = "Cliente.findByNombre", query = "SELECT c FROM Cliente c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Cliente.findByApellido", query = "SELECT c FROM Cliente c WHERE c.apellido = :apellido"),
    @NamedQuery(name = "Cliente.findByDireccion", query = "SELECT c FROM Cliente c WHERE c.direccion = :direccion"),
    @NamedQuery(name = "Cliente.findByTelefono", query = "SELECT c FROM Cliente c WHERE c.telefono = :telefono"),
    @NamedQuery(name = "Cliente.findByNroDocumento", query = "SELECT c FROM Cliente c WHERE c.nroDocumento = :nroDocumento")})
public class Cliente implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCliente")
    private List<PresupuestoCab> presupuestoCabList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCliente")
    private List<Reclamo> reclamoList;
    @OneToMany(mappedBy = "idCliente")
    private List<InstalacionCab> instalacionCabList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCliente")
    private List<OrdenTrabajoCab> ordenTrabajoCabList;
    @Size(max = 30)
    @Column(name = "nro_documento")
    private String nroDocumento;
    @JoinColumn(name = "id_tipo_cliente", referencedColumnName = "id_tipo_cliente")
    @ManyToOne
    private TipoCliente idTipoCliente;
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_cliente")
      @GeneratedValue(generator="CliSeq") 
    @SequenceGenerator(name="CliSeq",sequenceName="id_cliente_cliente_seq", allocationSize=1) 
    private BigDecimal idCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "apellido")
    private String apellido;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "direccion")
    private String direccion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "telefono")
    private String telefono;
    @JoinColumn(name = "id_ciudad", referencedColumnName = "id_ciudad")
    @ManyToOne(optional = false)
    private Ciudad idCiudad;
    @JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
    @ManyToOne(optional = false)
    private Estado idEstado;
    @JoinColumn(name = "id_tipo_docu", referencedColumnName = "id_tipo_docu")
    @ManyToOne(optional = false)
    private TipoDocumento idTipoDocu;

    public Cliente() {
    }

    public Cliente(BigDecimal idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente(BigDecimal idCliente, String nombre, String apellido, String direccion, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public BigDecimal getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(BigDecimal idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Ciudad getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Ciudad idCiudad) {
        this.idCiudad = idCiudad;
    }

    public Estado getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Estado idEstado) {
        this.idEstado = idEstado;
    }

    public TipoDocumento getIdTipoDocu() {
        return idTipoDocu;
    }

    public void setIdTipoDocu(TipoDocumento idTipoDocu) {
        this.idTipoDocu = idTipoDocu;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCliente != null ? idCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.idCliente == null && other.idCliente != null) || (this.idCliente != null && !this.idCliente.equals(other.idCliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Cliente[ idCliente=" + idCliente + " ]";
    }

    public String getNroDocumento(){
        return this.nroDocumento;
    }
    
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public TipoCliente getIdTipoCliente() {
        return idTipoCliente;
    }

    public void setIdTipoCliente(TipoCliente idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }


    @XmlTransient
    public List<Reclamo> getReclamoList() {
        return reclamoList;
    }

    public void setReclamoList(List<Reclamo> reclamoList) {
        this.reclamoList = reclamoList;
    }

    @XmlTransient
    public List<InstalacionCab> getInstalacionCabList() {
        return instalacionCabList;
    }

    public void setInstalacionCabList(List<InstalacionCab> instalacionCabList) {
        this.instalacionCabList = instalacionCabList;
    }

    @XmlTransient
    public List<OrdenTrabajoCab> getOrdenTrabajoCabList() {
        return ordenTrabajoCabList;
    }

    public void setOrdenTrabajoCabList(List<OrdenTrabajoCab> ordenTrabajoCabList) {
        this.ordenTrabajoCabList = ordenTrabajoCabList;
    }

    @XmlTransient
    public List<PresupuestoCab> getPresupuestoCabList() {
        return presupuestoCabList;
    }

    public void setPresupuestoCabList(List<PresupuestoCab> presupuestoCabList) {
        this.presupuestoCabList = presupuestoCabList;
    }
}