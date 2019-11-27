package awsTest;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class awsTest {
	/*
	 * Cloud Computing, Data Computing Laboratory Department of Computer Science
	 * Chungbuk National University
	 */
	static AmazonEC2 ec2;

	private static void init() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at (~/.aws/credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);
		}
		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-1") /*
																												 * check
																												 * *
																												 * console
																												 */
				.build();
	}

	public static void main(String[] args) throws Exception {
		init();
		Scanner scan = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		while (true) {
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance 2. available zones ");
			System.out.println(" 3. start instance 4. available regions ");
			System.out.println(" 5. stop instance 6. create instance ");
			System.out.println(" 7. reboot instance 8. list images ");
			System.out.println(" 9. terminated instances 10.Describe Key pair");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");

			number = scan.nextInt();
			switch (number) {
			case 1:
				listInstances();
				break;
			case 2:
				availableZones();
				break;
			case 3:
				startInstances();
				break;
			case 4:
				availableRegions();
				break;
			case 5:
				stopInstances();
				break;
			case 6:
				createInstances();
				break;
			case 7:
				rebootInstances();
				break;
			case 8:
				listImages();
				break;
			case 9 :
				terminatedInstances();
				break;
			case 10 :
				keypairDescribe();
				break;
			case 99:
				System.out.println("시스템을 종료합니다.");
				return;
			}
		}
	}

	private static void keypairDescribe() {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeKeyPairsResult response = ec2.describeKeyPairs();

		for(KeyPairInfo key_pair : response.getKeyPairs()) {
		    System.out.printf(
		        "[key pair with name] %s " +
		        "[fingerprint] %s",
		        key_pair.getKeyName(),
		        key_pair.getKeyFingerprint());
		    	System.out.println();
			
		}		
	}

	private static void listImages() {
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		
		DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");
		Collection<Image> images = ec2.describeImages(request).getImages();

		int flag_ami = 1;

		for (Image Im : images) {
			System.out.println(flag_ami + ")");
			System.out.printf("[Image ID] %s, " + "Owner ID] %s, "+ "[AMI Status] %s, ", 
					Im.getImageId(), Im.getOwnerId(),Im.getState());

			flag_ami++;
			System.out.println();
		}

		
	}

	public static void listInstances() {
		System.out.println("Listing instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					System.out.printf(
							"[id] %s, " + "[AMI] %s, " + "[type] %s, " + "[state] %10s, " + "[monitoring state] %s",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());
			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}

	public static void startInstances() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter instance id :");
		String instance_id = scan.nextLine();

		System.out.printf("Starting instances.... %s", instance_id);

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

		ec2.startInstances(request);
		System.out.println();

		System.out.printf("Successfully started instance %s", instance_id);
		System.out.println();

	}

	public static void stopInstances() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter instance id :");
		String instance_id = scan.nextLine();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

		ec2.stopInstances(request);

		System.out.printf("Successfully stoped instance %s", instance_id);

	}

	public static void terminatedInstances() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter instance id :");
		String instance_id = scan.nextLine();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(instance_id);

		ec2.terminateInstances(request);

		System.out.printf("Successfully terminated instance %s", instance_id);

	}
	
	public static void createInstances() {
		final String USAGE = "To run this example, supply an instance name and AMI image id\n"
				+ "Ex: CreateInstance <instance-name> <ami-image-id>\n";

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter ami id :");
		String ami_id = scan.nextLine();

		System.out.println("Enter key id :");
		String key_id = scan.nextLine();
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		RunInstancesRequest run_request = new RunInstancesRequest().withImageId(ami_id)
				.withInstanceType(InstanceType.T2Micro).withMaxCount(1).withMinCount(1).withKeyName(key_id);

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

		System.out.printf("Successfully started EC2 instance %s based on AMI %s", reservation_id, ami_id);

	}

	public static void rebootInstances() {
		Scanner scan = new Scanner(System.in);
		System.out.printf("Enter instance id :");
		String instance_id = scan.nextLine();

		System.out.printf("Rebooting .... %s", instance_id);

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);

		RebootInstancesResult response = ec2.rebootInstances(request);

		System.out.println();
		System.out.printf("Successfully rebooted instance %s", instance_id);

	}

	public static void availableZones() {
		int i = 0;
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		System.out.println();

		DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
		for (AvailabilityZone zone : zones_response.getAvailabilityZones()) {
			System.out.printf("[id] %s, " + "[region] %s, " + "[zone] %s, ", zone.getZoneId(), zone.getRegionName(),
					zone.getZoneName());
			System.out.println();

			i++;
		}

		System.out.printf("You have access to %d Availability Zones.", i);
		System.out.println();

	}

	public static void availableRegions() {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeRegionsResult regions_response = ec2.describeRegions();

		for (Region region : regions_response.getRegions()) {
			System.out.printf("[region] %s, " + "[endpoint] %s, ", region.getRegionName(), region.getEndpoint());
			System.out.println();

		}

	}

}