package com.example.model;

import  jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "cne")
public class Cne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nacionalidad", nullable = false)
    @NotBlank(message = "Nacionalidad es obligatoria")
    private String nacionalidad;

    @Column(name = "cedula", nullable = false)
    @NotBlank(message = "Cédula es obligatoria")
    private String cedula;

    @Column(name = "primer_apellido")
    private String primer_apellido;

    @Column(name = "segundo_apellido")
    private String segundo_apellido;

    @Column(name = "primer_nombre")
    private String primer_nombre;

    @Column(name = "segundo_nombre")
    private String segundo_nombre;

    @Column(name = "centro")
    private String centro;

    @Column(name = "nombre_completo")
    private String nombre_completo; 

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "foto")
    private String foto;

    @Column(name = "huellas")
    private String huellas;


    // Getters y Setters
        public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getPrimerApellido() {
        return primer_apellido;
    }

    public void setPrimerApellido(String primer_apellido) {
        this.primer_apellido = primer_apellido;
    }

    public String getSegundoApellido() {
        return segundo_apellido;
    }

    public void setSegundoApellido(String segundo_apellido) {
        this.segundo_apellido = segundo_apellido;
    }

    public String getPrimerNombre() {
        return primer_nombre;
    }

    public void setPrimerNombre(String primer_nombre) {
        this.primer_nombre = primer_nombre;
    }

    public String getSegundoNombre() {
        return segundo_nombre;
    }

    public void setSegundoNombre(String segundo_nombre) {
        this.segundo_nombre = segundo_nombre;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getNombreCompleto() {
        return nombre_completo;
    }

    public void setNombreCompleto(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHuellas() {
        return huellas;
    }

    public void setHuellas(String huellas) {
        this.huellas = huellas;
    }
    public Cne() {}
}
