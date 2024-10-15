package com.jeff.booksapi.service;

    public interface iConvierteDatos {
        <T> T obtenerDatos(String json, Class<T> clase);
    }

