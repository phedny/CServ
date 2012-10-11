package nl.limesco.cserv.util.dm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UnavailableOSGiService implements InvocationHandler {

	private final Constructor<?> constructor;
	
	private final Object[] arguments;
	
	private UnavailableOSGiService(Class<? extends Throwable> throwable, Object[] arguments) {
		Constructor<?> usableCtor = null;
		for (Constructor<?> ctor : throwable.getConstructors()) {
			if (isUsableConstructor(ctor, arguments)) {
				if (usableCtor != null) {
					throw new IllegalArgumentException("More than one constructor accepts the given set of arguments");
				}
				usableCtor = ctor;
			}
		}
		
		this.arguments = arguments;
		this.constructor = usableCtor;
	}
	
	public static <T> T newInstance(Class<T> serviceClass, Class<? extends Throwable> throwable, Object... arguments) {
		final UnavailableOSGiService handler = new UnavailableOSGiService(throwable, arguments);
		return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[] { serviceClass} , handler);
	}

	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
		throw (Throwable) constructor.newInstance(arguments);
	}

	private static boolean isUsableConstructor(Constructor<?> ctor, Object[] arguments) {
		final Class<?>[] paramTypes = ctor.getParameterTypes();
		if (arguments.length == paramTypes.length) {
			for (int i = 0; i < paramTypes.length; i++) {
				if (!paramTypes[i].isAssignableFrom(arguments[i].getClass())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
}
