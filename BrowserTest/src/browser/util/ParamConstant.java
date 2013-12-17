/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.util;

/**
 *
 * @author Dimas
 */
public class ParamConstant {
    
    // Configuration Class //
    public static final String welcomePage = "c:\\xulrunner\\welcome.html";
    public static final String tempHTML = "C:\\browser_test\\temp.html";
    public static final String xulrunnerPath = "C:\\";
    public static final String profilePath = "C:\\profile";
    public static final String urlSelector = "javascript:(function(){var s=document.createElement('div');s.innerHTML='Loading...';s.style.color='black';s.style.padding='20px';s.style.position='fixed';s.style.zIndex='9999';s.style.fontSize='3.0em';s.style.border='2px solid black';s.style.right='40px';s.style.top='40px';s.setAttribute('class','selector_gadget_loading');s.style.background='white';document.body.appendChild(s);s=document.createElement('script');s.setAttribute('type','text/javascript');s.setAttribute('src','http://localhost:8080/selectorgadget/lib/selectorgadget.js?raw=true');document.body.appendChild(s);})();";
    public static final String jdbcURI = "jdbc:sqlite:F:\\database.db";
    
}
