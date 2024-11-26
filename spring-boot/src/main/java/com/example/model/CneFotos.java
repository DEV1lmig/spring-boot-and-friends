package com.example.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cne_fotos")
public class CneFotos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cedula", nullable = false)
    private String cedula;

    @Column(name = "foto", nullable = false)
    private String foto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cedula", referencedColumnName = "cedula", insertable = false, updatable = false, unique = true)
    @JsonIgnore
    private Cne cne;

    // Constructor
    public CneFotos() {}

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Cne getCne() {
        return cne;
    }

    public void setCne(Cne cne) {
        this.cne = cne;
    }
}
