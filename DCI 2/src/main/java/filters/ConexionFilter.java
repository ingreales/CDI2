package org.jan.apiservlet.webapp.headers.filters;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.jan.apiservlet.webapp.headers.services.ServiceJdbcException;
import org.jan.apiservlet.webapp.headers.util.ConexionBaseDatosDS;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


@WebFilter("/*")
public class ConexionFilter implements Filter {
    @Inject
    @Named("conn")
    private Connection conn;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        try (Connection connRequest = this.conn){

            if (connRequest.getAutoCommit()){
                connRequest.setAutoCommit(false);
            }
            try {
                chain.doFilter(request,response);
                connRequest.commit();
            }catch (SQLException | ServiceJdbcException e){
                connRequest.rollback();
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }
}
