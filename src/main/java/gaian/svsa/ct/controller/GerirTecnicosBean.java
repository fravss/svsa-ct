package gaian.svsa.ct.controller;



import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.application.NavigationHandler;
import org.apache.commons.codec.digest.DigestUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Log4j
@Named
@RequestScoped 
@Setter
public class GerirTecnicosBean {
    @Inject
    private LoginBean loginBean;

    private static final String ALGORITMO_AES = "AES";
    private static final String CHAVE = "chaveSecreta1234";
    
    public void navegarParaFuncionalidade() {
        try {
        	
       	
        	String cod = loginBean.getUsuario().getCodigo().toString();
        	
        	
        	
        	log.info("o hash code " + cod);
      
        	String dadosCriptografados = criptografar(cod);
        	
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            
            Cookie sessionCookie = new Cookie("SESSIONID", dadosCriptografados);          
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);  // Se HTTPS estiver habilitado
            sessionCookie.setMaxAge(1800);  // 30 minutos
            sessionCookie.setPath("/"); // Define o caminho do cookie
   
            log.info("qual o cookie" + sessionCookie.getName());

  
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.addCookie(sessionCookie);

            FacesContext context = FacesContext.getCurrentInstance();
			String baseUrl = context.getExternalContext().getRequestScheme() + "://" +
			                 context.getExternalContext().getRequestServerName() + ":" +
			                 context.getExternalContext().getRequestServerPort();
			String redirectUrl = baseUrl + "/svsa-ep";
			
			context.getExternalContext().redirect(redirectUrl);

            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            log.error("Erro ao redirecionar para a URL externa.", e);
        }
    }
    
    public static String criptografar(String dados) throws Exception {
        SecretKey secretKey = new SecretKeySpec(CHAVE.getBytes(), ALGORITMO_AES);
        Cipher cipher = Cipher.getInstance(ALGORITMO_AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] dadosCriptografados = cipher.doFinal(dados.getBytes());
        return DatatypeConverter.printBase64Binary(dadosCriptografados);
    }


    public static String descriptografar(String dadosCriptografados) throws Exception {
        SecretKey secretKey = new SecretKeySpec(CHAVE.getBytes(), ALGORITMO_AES);
        Cipher cipher = Cipher.getInstance(ALGORITMO_AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] dadosDescriptografados = cipher.doFinal(DatatypeConverter.parseBase64Binary(dadosCriptografados));
        return new String(dadosDescriptografados);
    }
}
