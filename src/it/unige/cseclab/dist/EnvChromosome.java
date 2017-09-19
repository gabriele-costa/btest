package it.unige.cseclab.dist;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lagodiuk.ga.Chromosome;

import it.unige.cseclab.stim.TestChromosome;

public class EnvChromosome implements Chromosome<EnvChromosome> {
	
	
	private final int START_LEN = 4;
	private final String ALPHABET = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890.\\/()&%$,?!\"\'=@#-+*<>_:;|";
	
	Map<String, Object> env;
	
	public EnvChromosome(Map<String, Object> env) {
		super();
		this.env = env;
	}

	@Override
	public List<EnvChromosome> crossover(EnvChromosome mother) {
		
		EnvChromosome father = this;
		
		Map<String,Object> bEnv = new HashMap<String,Object>();
		Map<String,Object> gEnv = new HashMap<String,Object>();
		
		Random r = new Random();
		
		for(String k : env.keySet()) {
			if(r.nextBoolean()) {
				bEnv.put(k, father.env.get(k));
				gEnv.put(k, mother.env.get(k));
			}
			else {
				gEnv.put(k, father.env.get(k));
				bEnv.put(k, mother.env.get(k));
			}
		}
		
		EnvChromosome boy = new EnvChromosome(bEnv);
		EnvChromosome girl = new EnvChromosome(gEnv);
		
		return Arrays.asList(boy, girl);
	}

	@Override
	public EnvChromosome mutate() {
		Map<String, Object> mEnv = new HashMap<String, Object>();
		mEnv.putAll(env);
		
		mutate(mEnv);
		
		return new EnvChromosome(mEnv);
	}

	private void mutate(Map<String, Object> mEnv) {
		ArrayList<String> keyList = new ArrayList<>();
		keyList.addAll(mEnv.keySet());
		
		Random r = new Random();
		
		int loc = r.nextInt(keyList.size());
		
		String key = keyList.get(loc);
		Object val = mEnv.get(key);
		
		if(val instanceof Boolean) {
			mEnv.put(key, r.nextBoolean());
		} else if(val instanceof Integer) {
			mEnv.put(key, r.nextInt());
		} else if(val instanceof Double) {
			mEnv.put(key, r.nextDouble());
		} else if(val instanceof String) {
			mEnv.put(key, randomString((String) val, r));
		} else {
			mEnv.put(key, randomObject(r));
		}
	}

	private String randomString(String s, Random r) {
		if(s == null) {
			return createString(r);
		}
		else if(s.length() == 0) {
			int	w = r.nextInt(s.length() + 1);
			
			if(w == 0) {
				return s + ALPHABET.charAt(r.nextInt(ALPHABET.length()));
			} else {
				char[] ca = s.toCharArray();
				ca[w-1] = ALPHABET.charAt(r.nextInt(ALPHABET.length()));
				return new String(ca);
			}
		}
		else {
			int w = r.nextInt(s.length() + 2);
			
			if(w == 0) {
				return s.substring(0, s.length()-1);
			} else if(w == 1) {
				return s + ALPHABET.charAt(r.nextInt(ALPHABET.length()));
			} else {
				char[] ca = s.toCharArray();
				ca[w-2] = ALPHABET.charAt(r.nextInt(ALPHABET.length()));
				return new String(ca);
			}
		}
	}

	private String createString(Random r) {
		int w = r.nextInt(START_LEN + 1);
		if(w == 0)
			return null;
		
		String s = "";
		
		for(int i = 0; i < w-1; i++) {
			s += ALPHABET.charAt(r.nextInt(ALPHABET.length()));
		}
		
		return s;
	}

	private Object randomObject(Random r) {
		if(r.nextBoolean()) {
			return null;
		}
		else {
			return new Object();
		}
	}
}
