
package seker.common.net.transport;

/**
 * 
 * @author 
 * 
 */
public interface ITransport {
    
    public void start(ITransportCallback dcallback);
    
    public void pause();
    
    public void resume();
    
    public void cancel();
}
