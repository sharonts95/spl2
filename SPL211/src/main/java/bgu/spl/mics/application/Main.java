package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		Gson gson=new Gson();
		Reader reader=new FileReader("input.json");
		Input input=gson.fromJson(reader, Input.class);

		Ewoks ewoks=Ewoks.getInstance(input.getEwoks());

		HanSoloMicroservice HanSolo=new HanSoloMicroservice();
		C3POMicroservice C3PO=new C3POMicroservice();
		R2D2Microservice R2D2=new R2D2Microservice(input.getR2D2());
		LandoMicroservice Lando=new LandoMicroservice(input.getLando());
		LeiaMicroservice Leia=new LeiaMicroservice(input.getAttacks());

		Thread HanSoloT=new Thread(HanSolo);
		Thread C3POT=new Thread(C3PO);
		Thread R2D2T=new Thread(R2D2);
		Thread LandoT=new Thread(Lando);
		Thread LeiaT=new Thread(Leia);

		HanSoloT.start();
		C3POT.start();
		R2D2T.start();
		LandoT.start();
		LeiaT.start();

		HanSoloT.join();
		C3POT.join();
		R2D2T.join();
		LandoT.join();
		LeiaT.join();

		try (FileWriter writer=new FileWriter("Output.json")){
			gson.toJson(Diary.getInstance(), writer);
		}catch (IOException e){
			e.printStackTrace();
		}

	}
}
