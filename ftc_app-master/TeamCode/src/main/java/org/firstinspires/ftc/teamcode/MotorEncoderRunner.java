package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class MotorEncoderRunner {

    private ArrayList<MotorEncoderMovement> motorEncoderMovements = null;
    private Map<String, ArrayList<MotorEncoderMovement>> movementsCache;
    private ElapsedTime runtime = new ElapsedTime();

    private static ArrayList<MotorEncoderRunner> allRunners = new ArrayList<MotorEncoderRunner>();

    private Telemetry telemetry = null;
    private LinearOpMode lopmode = null;

    public MotorEncoderRunner(LinearOpMode lopmode)
    {
        motorEncoderMovements = new ArrayList<MotorEncoderMovement>();
        movementsCache = new HashMap<>();
        allRunners.add(this);
        this.lopmode = lopmode;
        telemetry = lopmode.telemetry;
    }

    private void AddTelemetry(String s1)
    {
        if(telemetry != null)
        {
            telemetry.addLine(s1);
            telemetry.update();
        }
    }

    private void AddTelemetry(String caption, String value)
    {
        if(telemetry != null)
        {
            telemetry.addData(caption, value);
            telemetry.update();
        }
    }

    public void AddMovement(MotorEncoderMovement movement)
    {

        motorEncoderMovements.add(movement);
    }

    /*
     * Stores current movements in dictionary, overrides value at key
     */
    public void Cache(String key)
    {
        Cache(key, true);
    }

    /*
     * Stores current movements in dictionary, overrides value at key if override == true
     */
    public void Cache(String key, boolean override)
    {
        if(override)
        {
            movementsCache.put(key, motorEncoderMovements);
        } else if(!ExistsInCache(key))
        {
            movementsCache.put(key, motorEncoderMovements);
        }
    }

    /*
     * Loads movements from cache dictionary to current movements list (if value exists)
     * Returns false if no value exists for key
     */
    public boolean Load(String key)
    {
        if(!ExistsInCache(key))
        {
            return false;
        }

        motorEncoderMovements = movementsCache.get(key);
        return true;
    }

    public boolean ExistsInCache(Object key)
    {
        return (movementsCache.get(key) != null);
    }

    public MotorEncoderMovement AddMovement(DcMotor motor, double targetPosition, double speed)
    {
        MotorEncoderMovement newMovement = new MotorEncoderMovement(motor, targetPosition, speed, this.telemetry);
        motorEncoderMovements.add(newMovement);

        return newMovement;
    }

    public MotorEncoderMovement AddMovement(DcMotor motor, double targetPosition, double speed, boolean ignoreMotorCurrentPosition)
    {
        MotorEncoderMovement newMovement = new MotorEncoderMovement(motor, targetPosition, speed, this.telemetry, ignoreMotorCurrentPosition);
        motorEncoderMovements.add(newMovement);

        return newMovement;
    }

    public void AddMovements(ArrayList<MotorEncoderMovement> movements)
    {
        for(MotorEncoderMovement movement : movements)
        {
            motorEncoderMovements.add(movement);
        }
    }

    public void RemoveMovement(MotorEncoderMovement movement)
    {
        motorEncoderMovements.remove(movement);
    }

    public void RemoveMovementIndex(int index)
    {
        motorEncoderMovements.remove(index);
    }

    public int IndexOfMovement(MotorEncoderMovement movement)
    {
        return motorEncoderMovements.indexOf(movement);
    }

    public void Clear()
    {
        StopMovements();
        motorEncoderMovements.clear();
    }

    public void StartMovements(double cutoffTimeSeconds)
    {
        StartMovements(cutoffTimeSeconds, 1, 1, true);
    }

    public void StartMovements(double cutoffTimeSeconds, double speedMult, double directionMult)
    {
        StartMovements(cutoffTimeSeconds, speedMult, directionMult, true);
    }

    public void StartMovements(double cutoffTimeSeconds, boolean autoStop)
    {
        StartMovements(cutoffTimeSeconds, 1, 1, autoStop);
    }

    public void StartMovements(double cutoffTimeSeconds, double speedMult, double directionMult, boolean autoStop)
    {
        for(MotorEncoderMovement movement : motorEncoderMovements)
        {
            movement.SetUseEncoder();
            movement.Start();
        }

        if(autoStop) {
            runtime.reset();

            while (MotorsBusy() && lopmode.opModeIsActive()) {
                if (runtime.seconds() >= cutoffTimeSeconds) {
                    break;
                }
            }

            StopMovements();
        }
    }

    public void StopMovements()
    {
        for(MotorEncoderMovement movement : motorEncoderMovements)
        {
            movement.Stop();
        }
    }

    public void TryStopMovements()
    {
        if(!MotorsBusy())
        {
            StopMovements();
        }
    }

    public boolean MotorsBusy()
    {
        for (MotorEncoderMovement movement : motorEncoderMovements)
        {
            if (movement.isBusy())
            {
                return true;
            }
        }

        return false;
    }

    public static void StopAllRunners()
    {
        for (MotorEncoderRunner runner : allRunners)
        {
            runner.StopMovements();
        }
    }

}
