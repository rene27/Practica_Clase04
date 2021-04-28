package com.emergentes.controlador;

import com.emergentes.ConexionBD;
import com.emergentes.modelo.Libro;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String op;
        op = (request.getParameter("op") != null ) ? request.getParameter("op") : "list";
        
        ArrayList<Libro> lista = new ArrayList<Libro>();
        
        ConexionBD canal = new ConexionBD();
        Connection conn = canal.conectar();
        PreparedStatement ps;
        ResultSet rs;
        
        if(op.equals("list")){
            try{
                //Para listar los datos
                String sql = "SELECT * FROM libros";
                
                // Consultar de la seleccion y almacenarlo en una coleccion
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                
                while (rs.next()){
                    Libro lib = new Libro();
                    lib.setId(rs.getInt("id"));
                    lib.setIsbn(rs.getString("isbn"));
                    lib.setTitulo(rs.getString("titulo"));
                    lib.setCategoria(rs.getString("categoria"));
                    lista.add(lib);
                   
                }
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } catch (SQLException ex){
                System.out.println("Error en SQL: " + ex.getMessage());
            } finally {
                canal.desconectar();
            }
        }
        if (op.equals("nuevo")){
            Libro l = new Libro();
            request.setAttribute("libro", l);
            request.getRequestDispatcher("editar.jsp").forward(request, response);
        }
        if (op.equals("eliminar")){
            int id = Integer.parseInt(request.getParameter("id"));
            try {
                String sql = "delete from libros where id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id); //Parametro numero 1
                ps.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error en SQL: " + ex.getMessage());
            } finally {
                canal.desconectar();
            }
            response.sendRedirect("MainController");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String categoria = request.getParameter("categoria");
        
        Libro l = new Libro();
        l.setId(id);
        l.setIsbn(isbn);
        l.setTitulo(titulo);
        l.setCategoria(categoria);
        
        ConexionBD canal = new ConexionBD();
        Connection conn = canal.conectar();
        PreparedStatement ps;
        ResultSet rs;
        
        if (id == 0){
            try {
                String sql = "insert into libros (isbn,titulo,categoria) values (?,?,?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, l.getIsbn());
                ps.setString(2, l.getTitulo());
                ps.setString(3, l.getCategoria());
                ps.executeUpdate();
            } catch (SQLException ex){
                System.out.println("Error en SQL: " + ex.getMessage());
            } finally {
                canal.desconectar();
            }
        }
        response.sendRedirect("MainController");
    }

}
