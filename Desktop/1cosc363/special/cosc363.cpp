//  ========================================================================
//  COSC363: Computer Graphics (2016);  University of Canterbury.
//
//  FILE NAME: Model3D.cpp
//  See Lab01.pdf for details
//
//  Program to load a mesh file in OFF format.
//  Assumptions: 
//      1.  The model consists of triangles only.
//      2.  The OFF file does not contain comment lines.
//  ========================================================================
 
#include <iostream>
#include <fstream>
#include <climits>
#include <math.h> 
#include <GL/freeglut.h>
#include "loadTGA.h"

using namespace std;
GLuint txId[4];  //Texture ID


//~ int count = 0;
//~ int step = 3;
float zack_step = 1.5;

float turning_v = 140.0;
float walking_v = 70;


//~ bool forward = true;

float zack_rotate_head_range = 15;


//--Globals ---------------------------------------------------------------
float PI = 3.1415926535897932384626433832795;
//~ float *x, *y, *z;  //vertex coordinate arrays
//~ int *t1, *t2, *t3; //triangles

float *xf1, *yf1, *zf1;  //vertex coordinate arrays
int *t1f1, *t2f1, *t3f1; //triangles

float *xf2,*yf2,*zf2;
int *t1f2,*t2f2,*t3f2;



float *xf3,*yf3,*zf3;
int *t1f3,*t2f3,*t3f3;
bool want_teapot = false;
bool turn_toggle = false;
int nvrtf1,ntrif1;
int nvrtf2,ntrif2;
int nvrtf3,ntrif3;
//int nvrt, ntri;    //total number of vertices and triangles
float cam_hgt = 400; //100Camera height
float angle = 60.0;  //50Rotation angle for viewing
float xmin1, xmax1, ymin1, ymax1; //min, max values of  object coordinates
float xmin2, xmax2, ymin2, ymax2, zmin2, zmax2; //min, max values of  object coordinates
int yes = 0;
float zoomFactor = 35;
float halo_pos = 0;
float halo_pos_x = 0;
float halo_pos_y = 3.0;
float halo_pos_z = 40.0;
bool halo_flag = false;
bool halo_jumped = false;
float halo_jump_height = 20;
float cam_rotate = 0;
float cam_zoom = 300; //300bigger->zoom out
float cam_x = 0;
float cam_x_walk = 0;
float xx = 0;
float yy = 0;
float fireBall_pos_x = 38.88;
float fireBall_pos_y = 64.0;
float reduceFishNet = 0.0;
float rotateFishNet = -40.0;
float spaceship_angle = 0.0;
//~ float spaceshipPos[3] = {300.0, 190.0, 0.0};
//float haloPos[3] = {0.0,3.0,0.0};
float spaceshipPos[3] = {0.0, -10.0, 0.0};
bool land = false;
float moon_angle = 0;
const int total_zack = 4;
float zackPos[total_zack][4];
float t = 0.;
bool halo_ear_up = false;
	float halo_ear_pos_x = -5.0;
	float halo_ear_pos_y = 5.3;
		float halo_ear_pos_xx = 0;
	float halo_ear_pos_yy = 0;
	float halo_ear_rotate = 0.0;
	float con = 1.0;
	bool halo_jumping = false;
	bool flat = false;
	bool up = true;
	bool down = false;
	bool to_flat = false;
	bool to_up = false;
	int side = -1;
	
	int halo_new_pos_x = 0.0;
	int halo_new_pos_y = 3.0;
		int halo_new_pos_z = 0.0;
		
int stage = 0;
int stage2 = 0;

		float toggle_y_n = false;
	float velocity = 300;
		float halo_jump_angle = 0;
float x_angle = 0;
float z_angle = 0;
bool halo_jump = false;
bool fishNet_flag = false;
bool teapotOut = false;
float left = 0;
int direction[4] = {0,0,0,0};
int temp[3] = {0,0,0};

int shelter_angle = 180;
bool shelter_open = true;
bool zack_on_ship = true;
float door_angle = 0.0;
float halo_size_x = 0.0;
float halo_size_y = 3.0;
float halo_size_z = 0.0;

const int total_halo_board = 7;
const int total_pot_board = 3;
const int total_zack_parts = 30;

const int halo_limit_len = 3;
const int halo_limit_len2 = 2;

float halo_size_const = 20.0;

const int halo_len = 40;
float halos[halo_len][3];
float haloColor[halo_len][3];

float zackBody[total_zack][total_zack_parts][4] = {{{0.0}}};
float zackBodyW[total_zack][total_zack_parts][4] = {{{0.0}}};

float haloBoardPos[total_halo_board][3];
float haloBoardTime[total_halo_board][3];
float teaPotBoardPos[total_pot_board][4];
bool toggle_y = false;
bool zack_walk = false;
float zack_turn = 0;
bool zack_dir = true;
bool hold_teapot = false;
bool makeATurn = false;
int walk_toggle[total_zack][total_zack_parts] = {{-1}};
//$za
float limit_time = -1.10;
const int total_path_blocks = 3;
float pathPos[total_path_blocks][4];

	float white[4] = {1., 1., 1., 1.};
	float black[4] = {0};
	    float grey[4] = {0.2, 0.2, 0.2, 1.0};
const int total_turning = 4;
float turning_point[total_turning][3];
bool door_open = false;
		int near = 50;
	int far = 1000;

float zackCountAndStep[total_zack][2] = {{0.0}};

bool forward[total_zack];
bool rightLegUp[total_zack];
float step_range[total_zack][2];

GLUquadric *q;    //Required for creating cylindrical objects
GLUquadric *qq;



//-- Computes the min, max values of coordinates  -----------------------

void computeMinMax(int& nvrt, float& xmin, float& xmax, float& ymin, float& ymax, float*& x, float*& y, float*& z, float& zmin, float& zmax)
{
	xmin = xmax = x[0];
	ymin = ymax = y[0];
	zmin = zmax = z[0];
	for(int i = 1; i < nvrt; i++)
	{
		if(x[i] < xmin) xmin = x[i];
		else if(x[i] > xmax) xmax = x[i];
		if(y[i] < ymin) ymin = y[i];
		else if(y[i] > ymax) ymax = y[i];
			if(z[i] < zmin) zmin = z[i];
		else if(z[i] > zmax) zmax = z[i];
	}
}

//--Function to compute the normal vector of a triangle with index tindx ----------
void normal(int tindx, float*& x, float*& y, float*& z, int*& t1, int*& t2, int*& t3)
{
	float x1 = x[t1[tindx]], x2 = x[t2[tindx]], x3 = x[t3[tindx]];
	float y1 = y[t1[tindx]], y2 = y[t2[tindx]], y3 = y[t3[tindx]];
	float z1 = z[t1[tindx]], z2 = z[t2[tindx]], z3 = z[t3[tindx]];
	float nx, ny, nz;
	nx = y1*(z2-z3) + y2*(z3-z1) + y3*(z1-z2); 
	ny = z1*(x2-x3) + z2*(x3-x1) + z3*(x1-x2);
	nz = x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2);
	glNormal3f(nx, ny, nz);
}

void loadTexture()	 
{
	glGenTextures(4, txId);   //Get 7 texture IDs 
	glBindTexture(GL_TEXTURE_2D, txId[0]);  //Use this texture name for the following OpenGL texture
	loadTGA("0forest.tga");
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);	// Linear Filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	// Linear Filtering
	glTexEnvi(GL_TEXTURE_ENV,
GL_TEXTURE_ENV_MODE, GL_REPLACE);

	glBindTexture(GL_TEXTURE_2D, txId[1]);  //Use this texture name for the following OpenGL texture
	loadTGA("1eclipse.tga");
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);	// Linear Filtering
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	
glTexEnvi(GL_TEXTURE_ENV,
GL_TEXTURE_ENV_MODE, GL_REPLACE);

	glBindTexture(GL_TEXTURE_2D, txId[2]);  //Use this texture name for the following OpenGL texture
	loadTGA("2sky.tga");
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);	// Linear Filtering
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	
	glTexEnvi(GL_TEXTURE_ENV,
GL_TEXTURE_ENV_MODE, GL_REPLACE);
		
	glBindTexture(GL_TEXTURE_2D, txId[3]);  //Use this texture name for the following OpenGL texture
	loadTGA("3reflectforest.tga");
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);	// Linear Filtering
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	
	glTexEnvi(GL_TEXTURE_ENV,
GL_TEXTURE_ENV_MODE, GL_REPLACE);
}
void printArray(){
	for(int i = 0; i < halo_len; i++){
			//printf("%f %f %f \n", halos[i][0], halos[i][1], halos[i][2]);
	
		}
	}
void initializeAliens(){
	zackCountAndStep[3][0] = 0.0;
zackCountAndStep[3][1] = 3.0;
	zackCountAndStep[1][0] = 0.0;
zackCountAndStep[1][1] = 5.0;
	for(int i = 0; i < total_halo_board; i++){
		haloBoardPos[i][0] = 83.0; //80-88
		haloBoardPos[i][1] = 3.0;
		haloBoardPos[i][2] = -5.5;

		haloBoardTime[i][0] = 0.040;
		haloBoardTime[i][1] = 0.03;
		haloBoardTime[i][2] = -0.651;
			//~ haloBoardTime[i][0] = 0.040;
		//~ haloBoardTime[i][1] = 0.0;
		//~ haloBoardTime[i][2] = -0.581;
	
}

	haloBoardPos[2][0] = 83.0; //80-88
		haloBoardPos[2][1] = 3.0;
		haloBoardPos[2][2] = -11.5;
	
		haloBoardPos[4][0] = 83.0; //80-88
		haloBoardPos[4][1] = 3.0;
		haloBoardPos[4][2] = -11.5;


		haloBoardPos[6][0] = 83.0; //80-88
		haloBoardPos[6][1] = 3.0;
		haloBoardPos[6][2] = -11.5;
		
	for(int i = 0; i < total_pot_board; i++){
		teaPotBoardPos[i][0] = 83.0; //80-88
		teaPotBoardPos[i][1] = 3.0;
		teaPotBoardPos[i][2] = -5.0;
		teaPotBoardPos[i][3] = 0.0;
}

	for(int i = 0; i < total_zack; i++){
		zackPos[i][0] = 100.0; //80 -10 //main one
		zackPos[i][1] = 5.8;
		zackPos[i][2] =130.0; //65 10
		zackPos[i][3] = 0.0;

			forward[i] = true;
			rightLegUp[i] = false;
			step_range[i][0] = 0.0;
			step_range[i][1] = 10.0;

}
	zackPos[1][0] = 10.0; //80 -10
	zackPos[1][2] = 160.0; //65 10
		
		zackPos[2][0] = 80.0; //80 -10
		zackPos[2][2] = 180.0; //65 10
		zackPos[2][3] = 90.0; //65 10
		
			zackPos[3][0] = -150.0; //80 -10 //on top
			zackPos[3][1] = 65.0; //80 -10
		zackPos[3][2] = 110.0; //65 10
			

	zackBody[3][0][1] = 8;
	zackBody[3][0][2] = -5;

	for(int i = 0; i < total_path_blocks; i++){
		
	pathPos[i][0] = 85.0;
	pathPos[i][1] = 0.0;
	pathPos[i][2] = 132.0;
	pathPos[i][3] = 0.0;
}

	for(int i = 0; i < total_turning; i++){
		
	turning_point[i][0] = 85.0;
	turning_point[i][1] = 0.0;
	turning_point[i][2] = 132.0;
}

}
void zackRotateHead(int id);
void zackRotateHeadR(int id);

void zackWalkS(int id);
void zackWalkL(int id);
void zackWalkR(int id);

void zackTurnFront(int id){
	printf("%f\n", zackPos[id][3]);
	if(zackPos[id][3] < 180){
		zackPos[id][3] += 20.0;
		}
	else if(zackPos[id][3] == 180.0){
		forward[id] = false;
		
		zackCountAndStep[id][0] = 0;
		zackCountAndStep[id][1] = step_range[id][1];
		
		glutTimerFunc(turning_v, zackWalkL, id);
		return;
	}		
	glutPostRedisplay();	
	glutTimerFunc(turning_v, zackTurnFront, id);
}

void zackTurnEnd(int id){

	if(zackPos[id][3] > 0){
		zackPos[id][3] -= 20.0;
		}
	else if(zackPos[id][3] == 0.0){

		forward[id] = true;
		zackCountAndStep[id][0] = 0;
		zackCountAndStep[id][1] = step_range[id][1];
		glutTimerFunc(turning_v, zackWalkL, id);
		return;
	}		
	glutPostRedisplay();	
	glutTimerFunc(turning_v, zackTurnEnd, id);
}


void zackRotateHeadBack(int id){
	if(zackBody[id][0][3] > 0){
		zackBody[id][0][3] -= 5.0;
	}
	else{
	
		glutTimerFunc(turning_v, zackRotateHeadR, id);
		return;
	}
	glutPostRedisplay();	
	glutTimerFunc(turning_v, zackRotateHeadBack, id);
}

void zackRotateHead(int id){
	if(zackBody[id][0][3] < zack_rotate_head_range){
		zackBody[id][0][3] += 5.0;
	}
	else{
		glutTimerFunc(turning_v, zackRotateHeadBack, id);
		return;
	}
	glutPostRedisplay();	
	glutTimerFunc(turning_v, zackRotateHead, id);
	}
	
void zackRotateHeadRBack(int id){
	if(zackBody[id][0][3] < 0){
		zackBody[id][0][3] += 5.0;
	}
	else{
		if(forward[id]){
			glutTimerFunc(walking_v, zackTurnFront, id);
		}
		else{
			glutTimerFunc(walking_v, zackTurnEnd, id);
			}
		return;
	}
	glutPostRedisplay();	
	glutTimerFunc(turning_v, zackRotateHeadRBack, id);
	}
	
void zackRotateHeadR(int id){

	if(zackBody[id][0][3] > -1*zack_rotate_head_range){
		zackBody[id][0][3] -= 5.0;
	}
	else{
	
		glutTimerFunc(turning_v, zackRotateHeadRBack, id);
		return;
	}
	glutPostRedisplay();	
	glutTimerFunc(turning_v, zackRotateHeadR, id);
	}


void zackWalkS(int id){
	if(rightLegUp[id]){
		if(zackBodyW[id][15][3] < 0){ //
		walk_toggle[id][3] = 1;
		walk_toggle[id][9] = -1;
		walk_toggle[id][15] = 1;
		walk_toggle[id][16] = -1;

		zackBodyW[id][3][3] += 3.0;
		zackBodyW[id][3][2] -= 0.4;
		zackBodyW[id][9][3] += 3.0;
		zackBodyW[id][9][2] += 0.4;
		zackBodyW[id][15][3] += 5.0;
		zackBodyW[id][15][1] += 0.1;
		zackBodyW[id][16][3] -= 5.0;
		zackBodyW[id][16][1] -= 0.2;
		if(forward[id]){
			zackPos[id][2] -= zack_step;//w
		}
		else{
			zackPos[id][2] += zack_step;//w
			}
	}
	if(zackBodyW[id][19][3] < 0){
		walk_toggle[id][19] = -1; 
		walk_toggle[id][20] = -1; 
		
		zackBodyW[id][19][3] += 5.0;
		zackBodyW[id][19][1] -= 0.1;
		zackBodyW[id][20][3] -= 5.0;
		zackBodyW[id][20][1] -= 0.2;
	}
	else{
		if(zackCountAndStep[id][0] < zackCountAndStep[id][1]){
			glutTimerFunc(walking_v, zackWalkL, id);
			}
		else{
			if(forward[id]){
				glutTimerFunc(walking_v, zackTurnFront, id);
			}
			else{
				glutTimerFunc(walking_v, zackTurnEnd, id);
				}
			}
	
		return;
	}
}
else{

	if(zackBodyW[id][19][3] < 0){
	 //arms
	 	walk_toggle[id][3] = -1;
		walk_toggle[id][9] = 1;
		walk_toggle[id][15] = -1;
		walk_toggle[id][16] = 1;
		walk_toggle[id][19] = 1;
		walk_toggle[id][20] = -1;

		zackBodyW[id][3][3] += 3.0;//w larm
		zackBodyW[id][3][2] += 0.4;
		zackBodyW[id][9][3] += 3.0;//w rarm
		zackBodyW[id][9][2] -= 0.4;

		zackBodyW[id][19][3] += 5.0;//w rleg
		zackBodyW[id][19][1] += 0.1;
		zackBodyW[id][20][3] -= 5.0;//part rleg
		zackBodyW[id][20][1] -= 0.2;
		
		if(forward[id]){
			zackPos[id][2] -= zack_step;//w
		}
		else{
			zackPos[id][2] += zack_step;//w
			}
		}
		if(zackBodyW[id][15][3] < 0){
		walk_toggle[id][15] = -1;
		walk_toggle[id][16] = -1;
		zackBodyW[id][15][3] += 5.0;//w lleg
		zackBodyW[id][15][1] -= 0.1;
		zackBodyW[id][16][3] -= 5.0;//part lleg
		zackBodyW[id][16][1] -= 0.2;
		}
		else{
			if(zackCountAndStep[id][0] < zackCountAndStep[id][1]){
				glutTimerFunc(walking_v, zackWalkR, id);
			}
			else{
				glutTimerFunc(walking_v, zackRotateHead, id);	
			}
			return;
	}
}
		glutPostRedisplay();	
		glutTimerFunc(walking_v, zackWalkS, id);
	}

void zackWalkL(int id){

	if(zackBodyW[id][19][3] > -45){
		walk_toggle[id][3] = -1;
		walk_toggle[id][9] = 1;
		walk_toggle[id][15] = -1;
		walk_toggle[id][16] = 1;
		walk_toggle[id][19] = 1;
		walk_toggle[id][20] = -1;
		
		zackBodyW[id][3][3] -= 3.0;//w larm
		zackBodyW[id][3][2] -= 0.4;
		zackBodyW[id][9][3] -= 3.0;//w rarm
		zackBodyW[id][9][2] += 0.4;

		zackBodyW[id][19][3] -= 5.0;//w rleg
		zackBodyW[id][19][1] -= 0.1;
		zackBodyW[id][20][3] += 5.0;//part rleg
		zackBodyW[id][20][1] += 0.2;
		
	
		if(forward[id]){
			zackPos[id][2] -= zack_step;//w
		}
		else{
			zackPos[id][2] += zack_step;//w
			}
		}
	
	if(zackBodyW[id][15][3] > -45){
		walk_toggle[id][15] = -1;
		walk_toggle[id][16] = -1;
		zackBodyW[id][15][3] -= 5.0;//w lleg
		zackBodyW[id][15][1] += 0.1;
		zackBodyW[id][16][3] += 5.0;//part lleg
		zackBodyW[id][16][1] += 0.2;
	}

	else{
		rightLegUp[id] = false;
		zackCountAndStep[id][0] += 1;
		glutTimerFunc(walking_v-40, zackWalkS, id);

		return;
	}
		glutPostRedisplay();	
		glutTimerFunc(walking_v, zackWalkL, id);
	}

void zackWalkR(int id){
	
	if(zackBodyW[id][15][3] > -45){
	 //arms
	 	walk_toggle[id][3] = 1;
		walk_toggle[id][9] = -1;
		walk_toggle[id][15] = 1;
		walk_toggle[id][16] = -1;
		zackBodyW[id][3][3] -= 3.0;//w larm
		zackBodyW[id][3][2] += 0.4;
		zackBodyW[id][9][3] -= 3.0;//w rarm
		zackBodyW[id][9][2] -= 0.4;
		
		zackBodyW[id][15][3] -= 5.0;//w lleg
		zackBodyW[id][15][1] -= 0.1;
		zackBodyW[id][16][3] += 5.0;//part lleg
		zackBodyW[id][16][1] += 0.2;
		if(forward[id]){
			zackPos[id][2] -= zack_step;//w
		}
		else{
			zackPos[id][2] += zack_step;//w
			}
		}
	
	if(zackBodyW[id][19][3] > -45){
		walk_toggle[id][19] = -1; 
		walk_toggle[id][20] = -1; 
		zackBodyW[id][19][3] -= 5.0;//w rleg
		zackBodyW[id][19][1] += 0.1;
		zackBodyW[id][20][3] += 5.0;//part rleg
		zackBodyW[id][20][1] += 0.2;
	}
	else{
		rightLegUp[id] = true;
		zackCountAndStep[id][0] += 1;

		glutTimerFunc(walking_v, zackWalkS, id);
		
		return;
		}

		glutPostRedisplay();	
		glutTimerFunc(walking_v, zackWalkR, id);
	}








	

void drawTeapot(int id){
	//$
	glPushMatrix();
	glColor3f(0., 1., 0.8);
	glTranslatef(teaPotBoardPos[id][0], teaPotBoardPos[id][1], teaPotBoardPos[id][2]);
	glScalef(4.0, 4.0, 4.0);
	glutSolidTeapot(1.0);
	glPopMatrix();
	}



void zackPickUpTeapot(int id){

	if(zackBody[id][3][3] < -10.0){
	
	zackBody[id][3][3] += 1.0; //leftarm
			zackBody[id][3][2]+= 0.2; //leftarm forward
	zackBody[id][3][1] += 0.2; //leftarm forward
		zackBody[id][9][3] += 1.0;//rightarm
		zackBody[id][9][2] += 0.2; //leftarm forward
		zackBody[id][9][1] += 0.2; //leftarm forward
		zackBody[id][15][3] += 1.0; //lthigh
			zackBody[id][19][3] += 1.0; //rthigh
		zackBody[id][16][2] += 0.2; //knee ball
		zackBody[id][17][3] += 1.8; //knee
				zackBody[id][20][2] += 0.2; //knee ball
			zackBody[id][21][3] += 1.8; //knee
			
		zackPos[0][1] -= 0.01; //wholebody
		
		zackBody[id][1][3] += 1.6;//body
		
	

			teaPotBoardPos[id][1] += 0.4;
			teaPotBoardPos[id][2] += 0.2;
			
	}

	
		
		else{
			if(zackBody[id][6][3] > -40){
						zackBody[id][6][3] -= 5.0;
			zackBody[id][12][3] -= 5.0;
					zackPos[0][1] -= 0.01; //wholebody
		zackBody[id][1][3] += 1.6;//body
				zackBody[id][15][3] += 1.0; //lthigh
			zackBody[id][19][3] += 1.0; //rthigh
					zackBody[id][16][2] += 0.2; //knee ball
		zackBody[id][17][3] += 1.8; //knee
				zackBody[id][20][2] += 0.2; //knee ball
			zackBody[id][21][3] += 1.8; //knee
			}
			else{
		zackTurnFront(id);
		return;
		}
	
	
	}
	glutPostRedisplay();
	    glutTimerFunc(120, zackPickUpTeapot, 0);
	}


void zackPickTeapot(int id){
	if(zackBody[id][3][3] > -20){
	
	zackBody[id][3][3] -= 1.0; //leftarm
			zackBody[id][3][2]-= 0.2; //leftarm forward
	zackBody[id][3][1] -= 0.2; //leftarm forward
		zackBody[id][9][3] -= 1.0;//rightarm
		zackBody[id][9][2] -= 0.2; //leftarm forward
		zackBody[id][9][1] -= 0.2; //leftarm forward
		zackBody[id][15][3] -= 1.0; //lthigh
			zackBody[id][19][3] -= 1.0; //rthigh
		zackBody[id][16][2] -= 0.2; //knee ball
		zackBody[id][17][3] -= 1.8; //knee
				zackBody[id][20][2] -= 0.2; //knee ball
			zackBody[id][21][3] -= 1.8; //knee
			
		zackPos[id][1] += 0.01; //wholebody
		
		zackBody[id][1][3] -= 1.6;//body


	
			zackBody[id][6][3] -= 1.0;
			zackBody[id][12][3] -= 1.0;
			
			

}
else{
	 glutTimerFunc(110, zackPickUpTeapot, 0);
	return ;
	}

	
	glutPostRedisplay();
	    glutTimerFunc(110, zackPickTeapot, 0);
}

void displayZackRFoot(){
		glPushMatrix();
		glColor3f(0.5, 0.5, 0.5);
		glTranslatef(3.5,-5.6,-7.0);
		glRotatef(-90.0,0,1.0,0);
		glScalef(2.9,1,1.7);
    //Construct the object model here using triangles read from OFF file
	glBegin(GL_TRIANGLES);
		for(int tindx = 0; tindx < ntrif1; tindx++)
		{
			
		   normal(tindx, xf1,yf1,zf1,t1f1,t2f1,t3f1);
			glVertex3d(xf1[t1f1[tindx]], yf1[t1f1[tindx]], zf1[t1f1[tindx]]);
		   glVertex3d(xf1[t2f1[tindx]], yf1[t2f1[tindx]], zf1[t2f1[tindx]]);
		   glVertex3d(xf1[t3f1[tindx]], yf1[t3f1[tindx]], zf1[t3f1[tindx]]);
		}			 
	glEnd();
		glPopMatrix();
	}

void displayHeadOfZack(){
	glPushMatrix();
	glTranslatef(-3,6.5,0);
	glScalef(1,1,1);
    //Construct the object model here using triangles read from OFF file
	glBegin(GL_TRIANGLES);
		for(int tindx = 0; tindx < ntrif3; tindx++)
		{
			
		   normal(tindx, xf3,yf3,zf3,t1f3,t2f3,t3f3);
			glVertex3d(xf3[t1f3[tindx]], yf3[t1f3[tindx]], zf3[t1f3[tindx]]);
		   glVertex3d(xf3[t2f3[tindx]], yf3[t2f3[tindx]], zf3[t2f3[tindx]]);
		   glVertex3d(xf3[t3f3[tindx]], yf3[t3f3[tindx]], zf3[t3f3[tindx]]);
		} 
	glEnd();
	glPopMatrix();
	}

void displayZackLArm(int id){
	glPushMatrix();//wArm
	glTranslatef(zackBodyW[id][3][0],zackBodyW[id][3][1], zackBodyW[id][3][2]);
	glTranslatef(-1.6,3.8, -2.0);
	glRotatef(walk_toggle[id][3]*zackBodyW[id][3][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.6,-1*3.8, -1*-2.0);
	
	glPushMatrix();//rotate upperarm
	glTranslatef(zackBody[id][3][0], zackBody[id][3][1], zackBody[id][3][2]);
	glTranslatef(-5.3, 12.1, -1.7);
	glRotatef(-1*zackBody[id][3][3],1.0,0.0,0.0);
	glTranslatef(-1*-5.3, -1*12.1, -1*-1.7);//-rotate upperarm
	
	glPushMatrix();//left upperarm
	q = gluNewQuadric();
	glColor3f(0.3, 0.2, 0.4);
	glTranslatef(-5.3, 12.1, -1.7);
	glRotatef(90.0,1.0,0.0,0.0);
	glScalef(1.0, 0.9, 1.4);
	gluCylinder(q, 2.0,1.5,3.0,5.0,5.0);
	gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
	
	glPushMatrix();//left elbow
	glColor3f(0., 0.4, 1.);
	glTranslatef(-5.3, 7.8, -1.6);
	glScalef(1.3, 0.7, 1.4);
	glutSolidSphere(1.0, 20.0, 20.0);
	glPopMatrix();

	
	glPushMatrix();//rotate lower arm
	glTranslatef(-5.2, 5.7, -1.7);
	glRotatef(-1*zackBody[id][6][3],1.0,0.0,0.0);
	glTranslatef(-1*-5.2, -1*5.7, -1*-1.7);//
	
	

	glPushMatrix();//left lower arm
	glColor3f(0., 0.2, 0.7);
	glTranslatef(-5.2, 5.7, -1.7);
	glScalef(2.2, 3.4, 2.2);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//left palm
	glColor3f(0.9, 0.2, 0.3);
	glTranslatef(-5.1, 3.3, -1.7);
	glScalef(2.0, 1.5, 3.0);
	glutSolidCube(1.0);
	glPopMatrix();
	glPopMatrix();//end
	glPopMatrix(); //end
		glPopMatrix(); //end


	}	

void displayZackRArm(int id){
				glPushMatrix();//wArm
	glTranslatef(zackBodyW[id][9][0],zackBodyW[id][9][1], zackBodyW[id][9][2]);
	glTranslatef(-1.6,3.8, -2.0);
	glRotatef(walk_toggle[id][9]*zackBodyW[id][9][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.6,-1*3.8, -1*-2.0);
	
	glPushMatrix();//rotate upperarm
	glTranslatef(zackBody[id][9][0], zackBody[id][9][1], zackBody[id][9][2]);
	glTranslatef(-5.3, 12.1, -1.7);
	glRotatef(-1*zackBody[id][9][3],1.0,0.0,0.0);
	glTranslatef(-1*-5.3, -1*12.1, -1*-1.7);//
	
	
	glPushMatrix();//right upper arm
	q = gluNewQuadric();
	glColor3f(0.3, 0.2, 0.4);
	glTranslatef(5.3, 12.1, -1.7);
	glRotatef(90.0,1.0,0.0,0.0);
	glScalef(1.0, 0.9, 1.4);
	gluCylinder(q, 2.0,1.5,3.0,5.0,5.0);
	gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
	
	
	glPushMatrix();//right elbow
	glColor3f(0., 0.4, 1.);
	glTranslatef(5.3, 7.8, -1.6);
	glScalef(1.3, 0.7, 1.4);
	glutSolidSphere(1.0, 20.0, 20.0);
	glPopMatrix();

	
		glPushMatrix();//rotate the lower arm
	glTranslatef(-5.2, 5.7, -1.7);
	glRotatef(-1*zackBody[id][12][3],1.0,0.0,0.0);
	glTranslatef(-1*-5.2, -1*5.7, -1*-1.7);//
	
	glPushMatrix();//right lower arm
	glColor3f(0., 0.2, 0.7);
	glTranslatef(5.2, 5.7, -1.7);
	glScalef(2.5, 3.4, 3);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//right palm
	glColor3f(0.9, 0.2, 0.3);
	glTranslatef(5.1, 3.3, -1.7);
	glScalef(2.0, 1.5, 3.0);
	glutSolidCube(1.0);
	glPopMatrix();
		glPopMatrix();//end
				glPopMatrix();//end
					glPopMatrix();//end

	}	

void displayZackLLeg(int id){
	glPushMatrix();//wleg
	glTranslatef(zackBodyW[id][15][0],zackBodyW[id][15][1], zackBodyW[id][15][2]);
	glTranslatef(-1.6,3.8, -2.0);
	glRotatef(walk_toggle[id][15]*zackBodyW[id][15][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.6,-1*3.8, -1*-2.0);
	
	glPushMatrix();//left thigh
	glTranslatef(zackBody[id][15][0],zackBody[id][15][1], zackBody[id][15][2]);
	glTranslatef(-1.6,3.8, -2.0);
	glRotatef(-1*zackBody[id][15][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.6, -1*3.8, -1*-2.0);//
	q = gluNewQuadric();
	glColor3f(0.78, 0.10, 0.59);
	glTranslatef(-1.6,3.8, -2.0);
	glRotatef(90.0,1.0,0.0,0.0);
	glScalef(1.0, 1.2, 1.0);
	gluCylinder(q, 2.0,1.5,3.0,5.0,5.0);
	gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
	
		glPushMatrix();//from knee to foot
		glRotatef(walk_toggle[id][16]*zackBodyW[id][16][3],1.0,0.0,0.0);
		glTranslatef(zackBodyW[id][16][0],zackBodyW[id][16][1], zackBodyW[id][16][2]);
		
			glPushMatrix();//left knee ball
			glTranslatef(zackBody[id][16][0], zackBody[id][16][1],  zackBody[id][16][2]);//translate
			glColor3f(0.7, 0.4, 0.2);
			glTranslatef(-1.7,0.4 , -1.9);
			glScalef(1.3, 1.0, 1.5);
			glutSolidSphere(1.0, 20.0, 20.0);
			glPopMatrix();
	
			

	glPushMatrix();//rotate left knee
	glTranslatef(-1.7,-3.7, -2);
	glRotatef(zackBody[id][17][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.7, -1*-3.7, -1*-2);//
	q = gluNewQuadric();//left knee
	glColor3f(0.1, 0.2, 0.3);
	glTranslatef(-1.7,-3.7, -2);
	glRotatef(-90.0,1.0,0.0,0.0);
	glScalef(1, 1.0, 1.3);
	gluCylinder(q, 2.0,1.5,3.0,5.0,5.0);
	gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
	
	glPushMatrix();//left foot
	glColor3f(0., 1., 1.);
	glTranslatef(-1.6, -4.7, -2.2);
	glScalef(3.5, 1.9, 3.6);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();
	glTranslatef(-3.2, 0, 0);
	displayZackRFoot();
	glPopMatrix();

		glPopMatrix();//end
				glPopMatrix();//end
	}

void displayZackRLeg(int id){
	glPushMatrix();//wleg
	glTranslatef(zackBodyW[id][19][0],zackBodyW[id][19][1], zackBodyW[id][19][2]);
	
	glTranslatef(-1.6,3.8, -2.0);
	glRotatef(walk_toggle[id][19]*zackBodyW[id][19][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.6,-1*3.8, -1*-2.0);
	
	glPushMatrix();//right leg
	q = gluNewQuadric();
	glColor3f(0.78, 0.10, 0.59);
	glTranslatef(1.6,3.8, -2.0);
	glRotatef(90.0,1.0,0.0,0.0);
	glScalef(1.0, 1.2, 1.0);
	gluCylinder(q, 2.0,1.5,3.0,5.0,5.0);
	gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
	

		glPushMatrix();//from knee to foot
		glRotatef(walk_toggle[id][20]*zackBodyW[id][20][3],1.0,0.0,0.0);
		glTranslatef(zackBodyW[id][20][0],zackBodyW[id][20][1], zackBodyW[id][20][2]);
		
	glPushMatrix();//right knee ball
	glTranslatef(zackBody[id][20][0], zackBody[id][20][1],  zackBody[id][20][2]);//translate
	glColor3f(0.7, 0.4, 0.2);
	glTranslatef(1.7,0.4 , -1.9);
	glScalef(1.3, 1.0, 1.5);
	glutSolidSphere(1.0, 20.0, 20.0);
	glPopMatrix();
	

	glPushMatrix();//rotate right knee
	glTranslatef(-1.7,-3.7, -2);
	glRotatef(zackBody[id][21][3],1.0,0.0,0.0);
	glTranslatef(-1*-1.7, -1*-3.7, -1*-2);//
	q = gluNewQuadric();//right knee
	glColor3f(0.1, 0.2, 0.3);
	glTranslatef(1.7,-3.7, -2);
	glRotatef(-90.0,1.0,0.0,0.0);
	glScalef(1, 1.0, 1.3);
	gluCylinder(q, 2.0,1.5,3.0,5.0,5.0);
	gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
	

	glPushMatrix();//right foot
	glColor3f(0., 1., 1.);
	glTranslatef(1.6, -4.7, -2.2);
	glScalef(3.5, 1.9, 3.6);
	glutSolidCube(1.0);
	glPopMatrix();
	
		glColor3f(1., 0.78, 0.06);		
		displayZackRFoot();
		glPopMatrix();
	glPopMatrix();
	}

void displayZack(int id){
	glPushMatrix();//wholeBody
	
	//~ float lgt_pos2[] = {20,10,-30,1};
	//~ float spot_dir[] = {-1.0f, -1.0f, 0.0f, 1.0f};
	glTranslatef(zackPos[id][0],zackPos[id][1], zackPos[id][2]);
	glRotatef(zackPos[id][3],0.0,1.0,0.0);
	glTranslatef(-1*zackPos[id][0],-1*zackPos[id][1], -1*zackPos[id][2]);
	glTranslatef(zackPos[id][0],zackPos[id][1], zackPos[id][2]);

	glPushMatrix();//Eye+head
	glRotatef(zackBody[id][1][3],1.0,0.0,0.0);
	glRotatef(zackBody[id][0][3],0.0,1.0,0.0);
	glPushMatrix();//Eye
	glColor3f(0.9, 0.2, 0.3);
    glTranslatef(0, 13.8, -5.0);
	glScalef(0.5, 0.5, 0.5);
	glutSolidDodecahedron();
	glPopMatrix();//-Eye
    glPushMatrix();//Head
    glTranslatef(0, 5.8, 0);
	displayHeadOfZack();
	glPopMatrix();//-Head
	glPopMatrix();//-Eye+head
	
	glPushMatrix();//Body
	glRotatef(zackBody[id][1][3],1.0,0.0,0.0);
	glColor3f(1., 0.78, 0.06);		
	glTranslatef(zackBody[id][1][0], zackBody[id][1][1], zackBody[id][1][2]);
	glTranslatef(0.0, 9.4, -1.8);
	glScalef(7.0, 5.3, 4.6);
	glutSolidCube(1.0);
	glPopMatrix();//-Body
	
	displayZackLArm(id);
	displayZackRArm(id);
	displayZackLLeg(id);
	displayZackRLeg(id);

	glPushMatrix();//Bottom
	glColor3f(0.20, 0.50, 0.26);	
	//~ glRotatef(zackBody[id][1][3],1.0,0.0,0.0); // necessary?
	glRotatef(zackBody[id][2][3],0.0,0.0,1.0);
	glTranslatef(zackBody[id][2][0], zackBody[id][2][1], zackBody[id][2][2]);	
	glTranslatef(0.0, 5.0, -1.9);
	glScalef(7.0, 3.5, 4.6);
	glutSolidCube(1.0);
	glPopMatrix();//-Bottom
    //~ glLightfv(GL_LIGHT1, GL_POSITION, lgt_pos2);   //light position
	//~ glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spot_dir); 
	glPopMatrix();//-wholeBody
		
		
	}
void haloJumpRightTimer(int id){
	
	
		
		//~ if(direction == 2 || direction == 3){
			//~ z_angle = 1.0;
			//~ x_angle = 0.0;
			//~ haloBoardPos[id][0] =  (velocity * tt_x) - (100*0.5*-9.81*tt_x*tt_x);
		//~ if(direction == 2){ //east
			//~ tt_x -= 0.010;
			//~ side = 1;
		//~ }
		//~ else if(direction == 3){ //west
			//~ tt_x += 0.010;
			//~ side = -1;
		//~ }
		//~ }
		//~ else if(direction == 0 || direction == 1){
			//~ z_angle = 0.0;
			//~ x_angle = 1.0;
			//~ haloBoardPos[id][2] =  (velocity * tt_z) - (100*0.5*-9.81*tt_z*tt_z);
		//~ if(direction == 0){ //north
		
			haloBoardTime[id][0] -= 0.010;
			side = 1;
		//~ }
		//~ else if(direction == 1){ //south
			//~ tt_z += 0.010;
			//~ side = 1;
		//~ }
		//~ }
		//$
		haloBoardPos[id][1] =  (velocity * haloBoardTime[id][1]) - (100*0.5*-9.81*haloBoardTime[id][1]*haloBoardTime[id][1]);
		haloBoardPos[id][0] =  (velocity * haloBoardTime[id][0]) - (100*0.5*-9.81*haloBoardTime[id][0]*haloBoardTime[id][0]);
		
			if(haloBoardPos[id][1] >= 10){
			toggle_y_n = true;
			halo_jump_angle = -45*side;
		}
		else if(haloBoardPos[id][1] <= 5){
			toggle_y_n = false;
			halo_jump_angle = 0;
		}
		if(toggle_y_n){
			haloBoardTime[id][1] -= 0.010;
			halo_jump_angle += (2*side);
		}
		else{
			haloBoardTime[id][1] += 0.010;
			halo_jump_angle -= (2*side);
		}
		//$
		printf("%f %f %f time \n", haloBoardTime[id][0], haloBoardTime[id][1], haloBoardTime[id][2]);
		glutPostRedisplay();	
		glutTimerFunc(300, haloJumpRightTimer, id);
		
}
void haloOnBoardOut(int id);
void haloJumpNorthTimer(int id){
		if(haloBoardTime[id][2] < limit_time){
			
			return;
		}
		//~ if(direction == 2 || direction == 3){
			//~ z_angle = 1.0;
			//~ x_angle = 0.0;
			//~ haloBoardPos[id][0] =  (velocity * tt_x) - (100*0.5*-9.81*tt_x*tt_x);
		//~ if(direction == 2){ //east
			//~ tt_x -= 0.010;
			//~ side = 1;
		//~ }
		//~ else if(direction == 3){ //west
			//~ tt_x += 0.010;
			//~ side = -1;
		//~ }
		//~ }
		//~ else if(direction == 0 || direction == 1){
			//~ z_angle = 0.0;
			//~ x_angle = 1.0;
			//~ haloBoardPos[id][2] =  (velocity * tt_z) - (100*0.5*-9.81*tt_z*tt_z);
		//~ if(direction == 0){ //north
			haloBoardTime[id][2] -= 0.010;
			side = -1;
		//~ }
		//~ else if(direction == 1){ //south
			//~ tt_z += 0.010;
			//~ side = 1;
		//~ }
		//~ }
		
		haloBoardPos[id][1] =  (velocity * haloBoardTime[id][1]) - (100*0.5*-9.81*haloBoardTime[id][1]*haloBoardTime[id][1]);
		haloBoardPos[id][2] =  (velocity * haloBoardTime[id][2]) - (100*0.5*-9.81*haloBoardTime[id][2]*haloBoardTime[id][2]);
		
			if(haloBoardPos[id][1] >= 10){
			toggle_y_n = true;
			halo_jump_angle = -45*side;
		}
		else if(haloBoardPos[id][1] <= 5){
			toggle_y_n = false;
			halo_jump_angle = 0;
		}
		if(toggle_y_n){
			haloBoardTime[id][1] -= 0.010;
			halo_jump_angle += (2*side);
		}
		else{
			haloBoardTime[id][1] += 0.010;
			halo_jump_angle -= (2*side);
		}
		//$
		

		printf("%f %f %f time \n", haloBoardTime[id][0], haloBoardTime[id][1], haloBoardTime[id][2]);
		glutPostRedisplay();	
		glutTimerFunc(300, haloJumpNorthTimer, id);
		
}


void haloOnBoardOut(int id){
	
//$
	

		
	if(stage < 7){

	if(stage == 2 || stage == 4 || stage == 6){
		haloBoardPos[id][2] += 0.4;
		teaPotBoardPos[stage2][2] += 0.4;
	
	if(haloBoardPos[id][2] > 12.0){	
		haloBoardPos[id][2] += 0.4;
		
		haloJumpNorthTimer(id);
			
		//~ haloJumpRightTimer(id);
		stage2 += 1;
		want_teapot = true;

		stage += 1;
	
		return;
	}
		}
		else{
			haloBoardPos[id][2] += 0.4;
				if(haloBoardPos[id][2] > 4.0){	
			haloJumpNorthTimer(id);
					stage += 1;
		
			return;
			
			}
		}

	}
	else{
		return;
		}

	//~ else if(haloBoardPos[id][2] > 12.0){
		//~ haloJumpNorthTimer(id);
		//~ teapotOut = true;
		//~ glutTimerFunc(300, zackPickTeapot, id);
		//~ return ;
	//~ }
	//~ 
	//~ haloBoardPos[id][2] += 0.7;
	
	//~ 
	glutPostRedisplay();	
	glutTimerFunc(300, haloOnBoardOut, id);
	
}

void displayHaloOnBoard(int id){
	glPushMatrix();//head
	glColor3f(haloColor[id][0], haloColor[id][1], haloColor[id][2]);
	glTranslatef(haloBoardPos[id][0], haloBoardPos[id][1], haloBoardPos[id][2]);
	glScalef(5.0, 5.0, 5.0);
	glutSolidCube(1.0);
	glPopMatrix();
	}

void initializeHalos(){
	for(int i = 0; i < halo_len; i++){
		for(int j=0; j < i; j++){

	halos[i][0] += halo_size_const;
}


	halos[i][1] = halo_size_y;
	halos[i][2] = halo_size_z;
	
}
printArray();
}
void haloEarFly(){
	
		if(halo_new_pos_y < 4.0){	//limit lower bound for jumping
		halo_jumped = false;
		up = false;
		to_up = false;
		flat =false;
		to_flat = false;
		down = true;
	}
	else if(halo_new_pos_y < 10.0){
		up = false;
		to_up = false;
		flat =false;
		to_flat = true;
		down = false;
		}
	else if(halo_new_pos_y == 15.0){
		up = false;
		to_up = false;
		flat =true;
		to_flat = false;
		down = false;
		}
	else if(halo_new_pos_y == 20.0){
				up = false;
		to_up = true;
		flat =false;
		to_flat = false;
		down = true;
	}
	else if(halo_new_pos_y == 30.0){ //limit upper bound for jumping
		halo_jumped = true;
		up = true;
		to_up = false;
		flat =false;
		to_flat = false;
		down = true;
		}
	if(halo_ear_up){	//the ear still up, time to go down
		halo_ear_rotate -= 10.0;
		}
	else{
		halo_ear_rotate += 10.0;
	
		}
	if(halo_ear_rotate == 0.0){ //limit lower bound for rising ear
		halo_ear_up = false;
		//~ halo_ear_pos_xx -= 0.2;	//ear should follow body
					//~ halo_ear_pos_yy -= 0.5;
	}
	else if(halo_ear_rotate == 90.0){
		halo_ear_up = true;
				//~ halo_ear_pos_xx += 0.2;	//ear should follow body
					//~ halo_ear_pos_yy += 0.5;

		}

}

void haloWalk(){

		//~ if(halo_new_pos_y >= 30){
			//~ toggle_y = true;
			//~ halo_jump_angle = -45*side;
		//~ }
		//~ else if(halo_new_pos_y <= 3){
			//~ toggle_y = false;
			//~ halo_jump_angle = 0;
		//~ }
		//~ if(toggle_y){
			//~ tt_y -= 0.008;
			//~ halo_jump_angle += (2*side);
		//~ }
		//~ else{
			//~ tt_y += 0.008;
			//~ halo_jump_angle -= (2*side);
		//~ }
		//~ 
//~ if(direction[2] || direction[3]){
//~ z_angle = 1.0;
//~ x_angle = 0.0;
//~ halo_new_pos_x =  (velocity * tt_x) - (100*0.5*-9.81*tt_x*tt_x);
//~ if(direction[2]){ //east
//~ tt_x -= 0.010;
//~ side = 1;
//~ }
//~ else if(direction[3]){ //west
//~ tt_x += 0.010;
//~ side = -1;
//~ }
//~ }
//~ else if(direction[0] || direction[1]){
//~ z_angle = 0.0;
//~ x_angle = 1.0;
//~ halo_new_pos_z =  (velocity * tt_z) - (100*0.5*-9.81*tt_z*tt_z);
//~ if(direction[0]){ //north
//~ tt_z -= 0.010;
//~ side = -1;
//~ }
//~ else if(direction[1]){ //south
//~ tt_z += 0.010;
//~ side = 1;
//~ }
//~ }
//~ halo_new_pos_y =  (velocity * tt_y) - (100*0.5*-9.81*tt_y*tt_y);
//~ haloEarFly();
	//~ glutPostRedisplay();		
}
void haloJump(int value){
	value += 1;
	int stopPt = 21;
	if(value < stopPt){
	//printf("%d \n", value);
			haloWalk();
		


	glutTimerFunc(98, haloJump, value);
	}
		
	else if(value == stopPt){
		halo_jump_angle = 0;
		halo_new_pos_y = 3;
		toggle_y = false;
		 glutPostRedisplay();
		}
}

void haloJumpUpDown(int value){
	if(halo_jumped){ //have been jumping then should come down
		halo_pos_y -= 1.0;
		halo_ear_pos_xx -= 0.1;	//ear should follow body
		halo_ear_pos_yy -= 0.1;
				halo_ear_pos_yy -= 0.5;			
		}
	else{
	
		halo_pos_y += 1.0;
		halo_ear_pos_xx += 0.1;
		halo_ear_pos_yy += 0.1;
			halo_ear_pos_yy += 0.5;
				
		}
	if(halo_new_pos_y == 3.0){	//limit lower bound for jumping
		halo_jumped = false;
		up = false;
		to_up = false;
		flat =false;
		to_flat = false;
		down = true;
	}
	else if(halo_new_pos_y == 5.0){
		up = false;
		to_up = false;
		flat =false;
		to_flat = true;
		down = false;
		}
	else if(halo_new_pos_y == 7.0){
		up = false;
		to_up = false;
		flat =true;
		to_flat = false;
		down = false;
		}
	else if(halo_new_pos_y == 9.0){
				up = false;
		to_up = true;
		flat =false;
		to_flat = false;
		down = true;
	}
	else if(halo_new_pos_y == 11.0){ //limit upper bound for jumping
		halo_jumped = true;
		up = true;
		to_up = false;
		flat =false;
		to_flat = false;
		down = true;
		}
	if(halo_ear_up){	//the ear still up, time to go down
		halo_ear_rotate -= 10.0;
		}
	else{
		halo_ear_rotate += 10.0;
	
		}
	if(halo_ear_rotate == 0.0){ //limit lower bound for rising ear
		halo_ear_up = false;
		//~ ////~ halo_ear_pos_xx -= 0.2;	//ear should follow body
			//~ //		//~ halo_ear_pos_yy -= 0.5;
	}
	else if(halo_ear_rotate == 90.0){
		halo_ear_up = true;
				//~ ////~ halo_ear_pos_xx += 0.2;	//ear should follow body
					//~ ////~ halo_ear_pos_yy += 0.5;

		}


	glutPostRedisplay();
	glutTimerFunc(100, haloJumpUpDown, 0);
	}

void haloEar(){
	if(to_up){
	glPushMatrix();//left ear
	glColor3f(0., 1., 1.);
	glTranslatef(halo_new_pos_x-4.2, halo_new_pos_y+4.2, 0);
	glRotatef(-45.0, 0.0, 0.0, 1.0);
	glTranslatef(-1*(halo_new_pos_x-5.0), -1*(halo_new_pos_y+2.5), 0);
	glTranslatef(halo_new_pos_x-5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//right ear
	glColor3f(0., 1., 1.);
	glTranslatef(halo_new_pos_x+4.2, halo_new_pos_y+4.2, 0);
	glRotatef(45.0, 0.0, 0.0, 1.0);
	glTranslatef(-1*(halo_new_pos_x-5.0), -1*(halo_new_pos_y+2.5), 0);
	glTranslatef(halo_new_pos_x-5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
}
	
	else if(to_flat){
	glPushMatrix();//left ear
	glColor3f(0., 1., 1.);
	glTranslatef(halo_new_pos_x-4.2, halo_new_pos_y+0.8, 0);
	glRotatef(45.0, 0.0, 0.0, 1.0);
	glTranslatef(-1*(halo_new_pos_x-5.0), -1*(halo_new_pos_y+2.5), 0);
	glTranslatef(halo_new_pos_x-5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//right ear
	glColor3f(0., 1., 1.);
	glTranslatef(halo_new_pos_x+4.2, halo_new_pos_y+0.8, 0);
	glRotatef(-45.0, 0.0, 0.0, 1.0);
	glTranslatef(-1*(halo_new_pos_x-5.0), -1*(halo_new_pos_y+2.5), 0);
	glTranslatef(halo_new_pos_x-5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
}
	
	else if(flat){
	glPushMatrix();//left ear
	glColor3f(0., 1., 1.);
	glTranslatef(halo_new_pos_x-5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//right ear
	glColor3f(0., 1., 1.);
	glTranslatef(halo_new_pos_x+5.0, halo_new_pos_y+2.5, 0);
	glRotatef(-90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
}
	
	else if(up){
	glPushMatrix();//left ear
	glColor3f(0., 0.2, 0.8); 
	glTranslatef(halo_new_pos_x, halo_new_pos_y, 0);
	glRotatef(90.0, 0.0, 0.0, 1.0);
	glTranslatef(halo_new_pos_x*-1, halo_new_pos_y*-1, 0);
	glTranslatef(halo_new_pos_x+5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//right ear
	glColor3f(0., 0.2, 0.8);
	glTranslatef(halo_new_pos_x, halo_new_pos_y+10, 0);
	glRotatef(-90.0, 0.0, 0.0, 1.0);
	glTranslatef(halo_new_pos_x*-1, halo_new_pos_y*-1, 0);
	glTranslatef(halo_new_pos_x+5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
}
	else if(down){
	glPushMatrix();//left ear
	glColor3f(0., 0.9, 0.3);
	glTranslatef(halo_new_pos_x, halo_new_pos_y-5.0, 0);
	glRotatef(90.0, 0.0, 0.0, 1.0);
	glTranslatef(halo_new_pos_x*-1, halo_new_pos_y*-1, 0);
	glTranslatef(halo_new_pos_x+5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
	
	glPushMatrix();//right ear
	glColor3f(0., 0.9, 0.3);
	glTranslatef(-1*halo_new_pos_x, halo_new_pos_y-5.0, 0);
	glRotatef(-90.0, 0.0, 0.0, 1.0);
	glTranslatef(halo_new_pos_x*-1, halo_new_pos_y*-1, 0);
	glTranslatef(halo_new_pos_x+5.0, halo_new_pos_y+2.5, 0);
	glRotatef(90.0, 1.0, 0.0, 0.0);
	glScalef(5.0, 5.0, 0.0);
	glutSolidCube(1.0);
	glPopMatrix();
}
	
	}
void displayHalo(){

	glPushMatrix();
	if(halo_jump){
		glTranslatef(halo_new_pos_x, halo_new_pos_y, halo_new_pos_z);
	}
	else{
		glTranslatef(halo_pos_x, halo_pos_y, halo_pos_z);
		}
//	glRotatef(halo_jump_angle, x_angle, 0.0, z_angle);

	haloEar();
	glPushMatrix();
	glScalef(5.0, 5.0, 5.0);
	glutSolidCube(1.0);
	glPopMatrix();
	glPopMatrix();

		

	
	
}


float randomiseHaloColor(){
	return static_cast <float> (rand()) / static_cast <float> (RAND_MAX);
	}

void displayOneHalo(float new_x, float new_y, float new_z){


		glTranslatef(new_x, new_y, new_z);

	glScalef(20.0, 20.0, 20.0);
	glutSolidCube(1.0);



	
}



void displayHalos(int amount){
	
	int round = amount / halo_limit_len;
	
	int height = 0;
	int depth = 0;

	for(int round_no=0; round_no <round; round_no++){

	for(int i=0; i < halo_limit_len-round_no; i++){
		glPushMatrix();
		glColor3f(0.20, 0.15, 0.14);
		displayOneHalo(halos[i][0],halos[i][1]+height,halos[i][2]+depth);
		glPopMatrix();

		}
			height = halo_size_const*(round_no+1);
	}

			
}

void drawBlocksInRow(int row_no, int row_amount){
	float dx = 0; float dy = 0; float dz = 0;
				glMaterialfv(GL_FRONT, GL_SPECULAR, black);
	for(int i=0; i <row_no; i++){
			glPushMatrix();

			//glRotatef(90,0.0,1.0,0.0);
			glTranslatef(dx,dy,dz);
		displayHalos(row_amount);
		
		glPopMatrix();
		dz += halo_size_const;
	}
	glMaterialfv(GL_FRONT, GL_SPECULAR, white);
	
}


void drawLand(int row_no, int block_no){


		glPushMatrix();
		glMaterialfv(GL_FRONT, GL_SPECULAR, black);
		glTranslatef(-100,0,130);

	glPushMatrix();//block
	glColor3f(0.20, 0.15, 0.14);
		glTranslatef(-50,23,20);
	glScalef(80.0, 60.0, 280.0);
	glutSolidCube(1.0);
	glPopMatrix();
	
	drawBlocksInRow(row_no, block_no);
			glPopMatrix();
			
				glPushMatrix();//block
					glTranslatef(-100,0,220-60);
				drawBlocksInRow(row_no, block_no);
					glPopMatrix();
					
								glPushMatrix();//block
					glTranslatef(-100,0,220-60-60);
				drawBlocksInRow(row_no, block_no);
					glPopMatrix();
					
					glPushMatrix();//block
					glTranslatef(-100,0,220-60-60-60);
				drawBlocksInRow(row_no, block_no);
					glPopMatrix();
		
						glPushMatrix();//block
							glTranslatef(-100,0,220-60-60-60-20);
						drawBlocksInRow(1,10);
					glPopMatrix();
		
			glPushMatrix();//block
					glTranslatef(-100-40,0,220+60);
				drawBlocksInRow(row_no, block_no);
					glPopMatrix();
					
						
			glPushMatrix();//block
					glTranslatef(-100-20,0,220);
				drawBlocksInRow(row_no, block_no);
					glPopMatrix();
					
					glMaterialfv(GL_FRONT, GL_SPECULAR, white);
		glPopMatrix();	
	}
	
	void drawSignalPost(){
			glPushMatrix();//
				glColor3f(0.30, 0.30, 0.0);
				glTranslatef(75,0,-40);
				
	glPushMatrix();
	glTranslatef(40,0,0);
	glPushMatrix();
	glTranslatef(0,46,-90);
	glScalef(3.0, 3.0, 3.0);
	glutSolidDodecahedron();
	glPopMatrix();
	glPushMatrix();
	glColor3f(0.30, 0.30, 0.0);
	glTranslatef(0,25,-90);
	glScalef(1.0, 45, 1.0);
	glutSolidCube(1.0);
	glPopMatrix();
	glutSolidDodecahedron();
	glPopMatrix();
				
	
	
	glPushMatrix();
	glTranslatef(-40,0,0);
	glPushMatrix();
	glTranslatef(0,46,-90);
	glScalef(3.0, 3.0, 3.0);
	glutSolidDodecahedron();
	glPopMatrix();
	glPushMatrix();
	glColor3f(0.30, 0.30, 0.0);
	glTranslatef(0,25,-90);
	glScalef(1.0, 45, 1.0);
	glutSolidCube(1.0);
	glPopMatrix();
	glutSolidDodecahedron();
	glPopMatrix();
	
		glPushMatrix();
	glTranslatef(0,0,45);
	glPushMatrix();
	glTranslatef(0,46,-90);
	glScalef(3.0, 3.0, 3.0);
	glutSolidDodecahedron();
	glPopMatrix();
	glPushMatrix();
	glColor3f(0.30, 0.30, 0.0);
	glTranslatef(0,25,-90);
	glScalef(1.0, 45, 1.0);
	glutSolidCube(1.0);
	glPopMatrix();
	glutSolidDodecahedron();
	glPopMatrix();
	
	
	
	glPopMatrix();
	
	}

//-- Loads mesh data in OFF format    -------------------------------------
void loadMeshFile(char* fname, float*& x, float*& y, float*& z, int*& t1, int*& t2, int*& t3, int& nvrt, int& ntri)  
{

	ifstream fp_in;
	int num, ne;

	fp_in.open(fname, ios::in);
	if(!fp_in.is_open())
	{
		cout << "Error opening mesh file" << endl;
		exit(1);
	}

	fp_in.ignore(INT_MAX, '\n');				//ignore first line
	fp_in >> nvrt >> ntri >> ne;			    // read number of vertices, polygons, edges
	

    x = new float[nvrt];                        //create arrays
    y = new float[nvrt];
    z = new float[nvrt];

    t1 = new int[ntri];
    t2 = new int[ntri];
    t3 = new int[ntri];

	for(int i=0; i < nvrt; i++)                         //read vertex list 
		fp_in >> x[i] >> y[i] >> z[i];

	for(int i=0; i < ntri; i++)                         //read polygon list 
	{
		
		fp_in >> num >> t1[i] >> t2[i] >> t3[i];
		if(num != 3)
		{
			cout << "ERROR: Polygon with index " << i  << " is not a triangle." << endl;  //not a triangle!!
			exit(1);
		}
	}

//$

	fp_in.close();
	cout << " File successfully read." << endl;
}


//------- Base of engine, wagons (including wheels) --------------------
void base()
{
    glColor4f(0.2, 0.2, 0.2, 1.0);   //The base is nothing but a scaled cube!
    glPushMatrix();
      glTranslatef(0.0, 4.0, 0.0);
      glScalef(20.0, 2.0, 10.0);     //Size 20x10 units, thickness 2 units.
      glutSolidCube(1.0);
    glPopMatrix();

    glPushMatrix();					//Connector between wagons
      glTranslatef(11.0, 4.0, 0.0);
      glutSolidCube(2.0);
    glPopMatrix();

    //Wheels
    glColor4f(0.5, 0., 0., 1.0);
    glPushMatrix();
      glTranslatef(-8.0, 2.0, 5.1);
      gluDisk(q, 0.0, 2.0, 20, 2);
    glPopMatrix();
    glPushMatrix();
      glTranslatef(8.0, 2.0, 5.1);
      gluDisk(q, 0.0, 2.0, 20, 2);
    glPopMatrix();
    glPushMatrix();
      glTranslatef(-8.0, 2.0, -5.1);
      glRotatef(180.0, 0., 1., 0.);
      gluDisk(q, 0.0, 2.0, 20, 2);
    glPopMatrix();
    glPushMatrix();
      glTranslatef(8.0, 2.0, -5.1);
      glRotatef(180.0, 0., 1., 0.);
      gluDisk(q, 0.0, 2.0, 20, 2);
    glPopMatrix();
}


//--- A rail wagon ---------------------------------------------------
void wagon()
{
    base();

    glColor4f(0.0, 1.0, 1.0, 1.0);
    glPushMatrix();
      glTranslatef(0.0, 10.0, 0.0);
      glScalef(18.0, 10.0, 10.0);
      glutSolidCube(1.0);
    glPopMatrix();
}

//~ void drawCannon()
//~ {
    //~ 
        	//~ glPushMatrix(); //canon
		//~ glTranslatef(-20,30.0,0.0);
	//~ glRotatef(30,0.0 , 0.0, 1.0);
		//~ glTranslatef(20,-30.0,0.0);
	//~ 
	//~ glScalef(1, 1, 1);
//~ 
	//~ 
	//~ glColor3f(0.4, 0.5, 0.4);
//~ 
    //~ //Construct the object model here using triangles read from OFF file
	//~ glBegin(GL_TRIANGLES);
		//~ for(int tindx = 0; tindx < ntrif1; tindx++)
		//~ {
	//~ 
				    //~ normal(tindx);
		   //~ glVertex3d(xf1[tf1[tindx]], yf1[tf1[tindx]], zf1[tf1[tindx]]);
		   //~ glVertex3d(xf1[tf2[tindx]], yf1[tf2[tindx]], zf1[tf2[tindx]]);
		   //~ glVertex3d(xf1[tf3[tindx]], yf1[tf3[tindx]], zf1[tf3[tindx]]);
	   //~ 
		//~ }
	//~ glEnd();
	//~ glPopMatrix();
	 	//~ glPushMatrix();//base
		//~ glTranslatef(-10,5,17);
			//~ glScalef(80, 10, 6);
			//~ glutSolidCube(1.0);
		//~ glPopMatrix();
		//~ 
		 	//~ glPushMatrix();		//base
			//~ glTranslatef(-20,25,17);
			//~ glScalef(40, 30, 6);
			//~ glutSolidCube(1.0);
			//~ 
		//~ glPopMatrix();
		//~ 
		//~ glPushMatrix();			
		//~ glTranslatef(-10,5,-17);	//base refl
			//~ glScalef(80, 10, 6);
			//~ glutSolidCube(1.0);
		//~ 
		//~ glPopMatrix();		
		 	//~ glPushMatrix();		//base refl
					//~ glTranslatef(-20,25,-17);
			//~ glScalef(40, 30, 6);
			//~ glutSolidCube(1.0);
				//~ 
		//~ glPopMatrix();	
			//~ 
	//~ glPushMatrix();
	//~ glTranslatef(fireBall_pos_x,fireBall_pos_y,0);
	//~ glutSolidSphere(5, 36, 18);
	//~ glPopMatrix();
//~ 
//~ }

void displayMoon() {
		glEnable(GL_TEXTURE_2D);
		gluQuadricTexture (q, GL_TRUE);

	glBindTexture(GL_TEXTURE_2D, txId[1]);
	
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	glPushMatrix();
	    glTranslatef(0.0, 0.0, -2.5);	 //Translate Earth along x-axis by 20 units	
	    glRotatef(moon_angle, 0, 1, 0);       //Rotation about polar axis of the Earth
	    glRotatef(-90., 1.0, 0., 0.0);   //make the sphere axis vertical
	    
	     glScalef(4.2,4.2,4.2);
	    gluSphere ( q, 3.0, 36, 17 );
    glPopMatrix();
    glDisable(GL_TEXTURE_2D);
	}

void drawFloor()
{
	float texture[8] = {0.40,0.10,
		0.40,0.58,
		1.0,0.10,
		1,0.58};
		
					float reflectTexture[8] = {0,0.10,
		0,0.58,
		0.76,0.10,
		0.76,0.58};
		float leftTexture[8] = {0,0.10,
		0,0.58,
		0.60,0.10,
		0.60,0.58};
	//~ float lowerTexture[8] = {0.40,0.10,
		//~ 0.40,0.58,
		//~ 0.60,0.10,
		//~ 0.60,0.58};
		
			float bottomTexture[8] = {0.10,0,
		0.10,0,
		0.90,0,
		0.90,0.16};
		
			//~ 
			
			
	float topTexture[8] = {0,0,
		0,1,
		1,0,
		1,1};
		
			glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, txId[3]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	glBegin(GL_QUAD_STRIP);
	glNormal3f(0, 1, 0);
	//left
		glTexCoord2f(leftTexture[0], leftTexture[1]);glVertex3f(-300, 0, 300);
		glTexCoord2f(leftTexture[2], leftTexture[3]);glVertex3f(-300, 300, 300);
			glTexCoord2f(leftTexture[4], leftTexture[5]);glVertex3f(-300, 0, -300); 
		glTexCoord2f(leftTexture[6], leftTexture[7]);glVertex3f(-300, 300, -300);
	glEnd();
	glDisable(GL_TEXTURE_2D);
	
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, txId[0]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	glBegin(GL_QUAD_STRIP);
	glNormal3f(0, 1, 0);
		//floor - bottom
	
	
	    glTexCoord2f(bottomTexture[0], bottomTexture[1]);glVertex3f(300, 0, 300);
	        	glTexCoord2f(bottomTexture[2], bottomTexture[3]);glVertex3f(300, 0, -300);
	        	  glTexCoord2f(bottomTexture[4], bottomTexture[5]);glVertex3f(-300, 0, 300);
	    	glTexCoord2f(bottomTexture[6], bottomTexture[7]);glVertex3f(-300, 0, -300);
	
	  
	glEnd();
	 glDisable(GL_TEXTURE_2D);
	  	
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, txId[0]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	glBegin(GL_QUAD_STRIP);
	glNormal3f(0, 1, 0);
		//~ //wall - back

	  glTexCoord2f(texture[0], texture[1]);glVertex3f(-300, 0, -300);
	   glTexCoord2f(texture[2], texture[3]);glVertex3f(-300, 300, -300);
	   	       	glTexCoord2f(texture[4], texture[5]);glVertex3f(300, 0, -300);
glTexCoord2f(texture[6], texture[7]);glVertex3f(300, 300, -300);
	glEnd();
	glDisable(GL_TEXTURE_2D);
	

	
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, txId[3]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	glBegin(GL_QUAD_STRIP);
	glNormal3f(0, 1, 0);
		 //wall - right
		glTexCoord2f(reflectTexture[0], reflectTexture[1]);glVertex3f(300, 0, -300);
	    glTexCoord2f(reflectTexture[2], reflectTexture[3]);glVertex3f(300, 300, -300);
		glTexCoord2f(reflectTexture[4], reflectTexture[5]);glVertex3f(300, 0, 300);
		glTexCoord2f(reflectTexture[6], reflectTexture[7]);glVertex3f(300, 300, 300);

	glEnd();
	glDisable(GL_TEXTURE_2D);
	

		glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, txId[3]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	glBegin(GL_QUAD_STRIP);
	glNormal3f(0, 1, 0);
	//wall - front
	glTexCoord2f(reflectTexture[0], reflectTexture[1]);glVertex3f(-300, 0, 300);
		glTexCoord2f(reflectTexture[2], reflectTexture[3]);glVertex3f(-300, 300, 300);
	    glTexCoord2f(reflectTexture[4], reflectTexture[5]);glVertex3f(300, 0, 300);
	       glTexCoord2f(reflectTexture[6], reflectTexture[7]);glVertex3f(300, 300, 300);

	glEnd();
	glDisable(GL_TEXTURE_2D);
	

	//~ glEnable(GL_TEXTURE_2D);
	//~ glBindTexture(GL_TEXTURE_2D, txId[2]);
	//~ glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	
	//~ glBegin(GL_QUAD_STRIP);
	//~ glNormal3f(0, 1, 0);
		    		//~ //top
	//~ glTexCoord2f(topTexture[0], topTexture[1]);glVertex3f(-300, 300, -300);
	//~ glTexCoord2f(topTexture[2], topTexture[3]);glVertex3f(-300, 300, 300);
	   //~ glTexCoord2f(topTexture[4], topTexture[5]);glVertex3f(300, 300, -300);
	   //~ glTexCoord2f(topTexture[6], topTexture[7]);glVertex3f(300, 300, 300);
	//~ glEnd();
	//~ glDisable(GL_TEXTURE_2D);


}
void drawGrid(int sx, int sz)
{
	glPushMatrix();
	glTranslatef(0.0,1.0,0.0);


	glNormal3f(0, 1, 0);
		glColor3f(0.5, 0.5, 0.5);
			
			

	glBegin(GL_QUADS);
	glMaterialfv(GL_FRONT, GL_SPECULAR, black);
	for(int x = (-1*sx); x <= sx; x += 20)
	{
		for(int z = (-1*sz); z <= sz; z += 20)
		{

			glVertex3f(x, 0, z);
			glVertex3f(x, 0, z+20);
			glVertex3f(x+20, 0, z+20);
			glVertex3f(x+20, 0, z);
			
		}
	}
	glEnd();
			glMaterialfv(GL_FRONT, GL_SPECULAR, white);
	glPopMatrix();
}

void drawPath(){
//$

	glMaterialfv(GL_FRONT, GL_SPECULAR, black);

	glPushMatrix();//path
	glTranslatef(pathPos[0][0],pathPos[0][1],pathPos[0][2]);
	drawGrid(140,122);
	glPopMatrix();
	
		glPushMatrix();//path
 glColor3f(0.6, 1.0, 0.8);

	glTranslatef(pathPos[1][0]+52,pathPos[1][1],pathPos[1][2]+33);
glRotatef(90,0.0,1.0,0.0);
glTranslatef(-1*pathPos[1][0],-1*pathPos[1][1],-1*pathPos[1][2]);
drawGrid(20,120);
	glPopMatrix();
	
		glMaterialfv(GL_FRONT, GL_SPECULAR, white);

	}
void openDoor(int value){
	if(door_angle > 85){
		 	door_open = true;
			//~ glutTimerFunc(300, haloOnBoardOut, 0);
			 //~ glutTimerFunc(300, zackWalk, 0);
			
		return ;
		}
	door_angle += 5;
	
	glutPostRedisplay();
	
    glutTimerFunc(300, openDoor, 0);
	}

void moveSpaceship(int value){
	
	if(!land){
		spaceshipPos[0] -= 1;
		spaceshipPos[1] -= 1;
	}
	else{
		 if(spaceshipPos[0] > 0){
			spaceshipPos[0] -= 1;
		}
		else{
				
				glutTimerFunc(110, openDoor, 0);
			
			return ;
			
			}
		
		
		}
	if(spaceshipPos[1] == -10){
		land = true;
	}

	glutPostRedisplay();
	
    glutTimerFunc(110, moveSpaceship, 0);
}

void drawSpaceship(){
		  
   	//$$
   	
   				float reflectTexture[8] = {0,0.3,
		0,1,
		0.3,0,
		1,1};
		
	glColor3f(0.4, 0.5, 0.4);
	glPushMatrix();
    //Construct the object model here using triangles read from OFF file
    			glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, txId[1]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	

	glBegin(GL_TRIANGLES);
glNormal3f(0, 1, 0);

		for(int tindx = 0; tindx < ntrif2; tindx++)
		{
			     normal(tindx, xf2,yf2,zf2,t1f2,t2f2,t3f2);
		
			glTexCoord2f(reflectTexture[0], reflectTexture[1]);glVertex3d(xf2[t1f2[tindx]], yf2[t1f2[tindx]], zf2[t1f2[tindx]]);
		  glTexCoord2f(reflectTexture[2], reflectTexture[3]); glVertex3d(xf2[t2f2[tindx]], yf2[t2f2[tindx]], zf2[t2f2[tindx]]);
		 glTexCoord2f(reflectTexture[4], reflectTexture[5]);  glVertex3d(xf2[t3f2[tindx]], yf2[t3f2[tindx]], zf2[t3f2[tindx]]);

		}
	glEnd();
	
	glDisable(GL_TEXTURE_2D);
    


    glPopMatrix();
	}    

void displaySpaceship(){
		//glLoadIdentity();
				glPushMatrix();
  
//$
    
		glRotatef(spaceship_angle,0.0,0.0,1.0);
	glTranslatef(spaceshipPos[0],spaceshipPos[1],spaceshipPos[2]);
	drawSpaceship();

    	    //~ glLightfv(GL_LIGHT1, GL_POSITION, lgt_pos3);   //light position
    //~ glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spot_dir2); 
	glPopMatrix();
	
			//~ glPushMatrix();
				//~ glTranslatef(-1*spaceshipPos[0],-1*spaceshipPos[1],-1*spaceshipPos[2]);
	//~ 
					//~ glutSolidCube(1.0);
    		    //~ glLightfv(GL_LIGHT1, GL_POSITION, lgt_pos2);   //light position
				//~ glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spot_dir); 
				//~ glPopMatrix();
	
		glPushMatrix();//door
		
	glColor3f(1., 0.78, 0.06);
		glRotatef(door_angle,1,0,0);		
	glTranslatef(85+spaceshipPos[0],5.1+spaceshipPos[1]+10.0,0+spaceshipPos[2]);
	glScalef(15.0, 10.3, 0.2);
	glutSolidCube(1.0);
	glPopMatrix();
	}
	
    
    
void openShelter(int value){
	if(shelter_open){
		if(shelter_angle < 20.0){
			shelter_open = false;
			shelter_angle = 0;
			glutPostRedisplay();
			return ;
		}
		else{
			shelter_angle -= 20.0;	
		}
	}
	 	else {
				if(shelter_angle > 160.0){
			shelter_open = true;
			shelter_angle = 180;
			glutPostRedisplay();
			return ;
		}
		else{
			shelter_angle += 20.0;	
		}
}

	value += 1;

	glutPostRedisplay();
	
    glutTimerFunc(300, openShelter, value);
	}

void displayStars(){
	

	glPushMatrix();
	
		glTranslatef(250,230,80);
		glScalef(2.0,2.0,2.0);
	
		displayMoon();
		
		glPushMatrix();
				glEnable(GL_TEXTURE_2D);
					gluQuadricTexture (q, GL_TRUE);
					glBindTexture(GL_TEXTURE_2D, txId[1]);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);	

		
		
		glPushMatrix();

	glColor3f(0.1, 0.5, 0.8);
	glTranslatef(0,-3.7, -2);
	glRotatef(-90.0,1.0,0.0,0.0);
	glScalef(1.5, 1.5, 1.5);
	gluCylinder(q, 15.0,11.0,2.0,60.0,60.0);
	//gluQuadricDrawStyle(q, GLU_FILL);
	glPopMatrix();
		glPopMatrix();
		gluQuadricTexture (q, GL_FALSE);
	glDisable(GL_TEXTURE_2D);
		   //~ glDisable(GL_TEXTURE_GEN_S); //enable texture coordinate generation
    //~ glDisable(GL_TEXTURE_GEN_T);
    
    	//~ glutSwapBuffers();
    	glPopMatrix();
	}


	

//--Display: ----------------------------------------------------------------------
//--This is the main display module containing function calls for generating
//--the scene.



void initializeHaloColor(){
	for(int i = 0; i < halo_len; i++){
	haloColor[i][0] = randomiseHaloColor();
	haloColor[i][1] = randomiseHaloColor();
	haloColor[i][2] = randomiseHaloColor();
}
	}


void timer(int value)
{
	moon_angle ++;
	if(angle > 360) moon_angle = 0;
	glutTimerFunc(400, timer, value);
	glutPostRedisplay();
}

void setProjectionMatrix ()
{
	printf("%f\n", zoomFactor);
   glMatrixMode(GL_PROJECTION);
   glLoadIdentity();
   gluPerspective (zoomFactor, 1, near, far);
   /* ...Where 'zNear' and 'zFar' are up to you to fill in. */
}

//------- Initialize OpenGL parameters -----------------------------------
void initialize()
{
	//~ float model_wid, model_hgt;
	float model_wid2, model_hgt2, model_vol2;

	loadMeshFile("Spaceship.off",xf2,yf2,zf2,t1f2,t2f2,t3f2,nvrtf2,ntrif2);	
	loadMeshFile("ZackFoot.off",xf1,yf1,zf1,t1f1,t2f1,t3f1,nvrtf1,ntrif1);
	loadMeshFile("headOfZack.off",xf3,yf3,zf3,t1f3,t2f3,t3f3,nvrtf3,ntrif3);	

			
			q = gluNewQuadric();
		loadTexture();
		


	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);	//Background colour

	glEnable(GL_LIGHTING);					//Enable OpenGL states
	glEnable(GL_LIGHT0);
		glEnable(GL_LIGHT1);
		
 	glEnable(GL_COLOR_MATERIAL);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_NORMALIZE);


	//~ computeMinMax(nvrtf1, xmin1, xmax1, ymin1, ymax1, xf1, yf1);
			    //Compute min, max values of x, y coordinates for defining camera frustum
		//~ 
	//~ model_wid = xmax1-xmin1;						//Model width and height
	//~ model_hgt = ymax1-ymin1;
	//~ xmin1 -= 0.2*model_wid;						//Extend minmax window further by 20% of its size.
	//~ xmax1 += 0.2*model_wid;
	//~ ymin1 -= 0.2*model_hgt;
	//~ ymax1 += 0.2*model_hgt;
		computeMinMax(nvrtf2, xmin2, xmax2, ymin2, ymax2, xf2, yf2, zf2, zmin2, zmax2);
		model_wid2 = xmax2-xmin2;						//Model width and height
	model_hgt2 = ymax2-ymin2;
	model_vol2 = zmax2-zmin2;
	xmin2 -= 0.2*model_wid2;						//Extend minmax window further by 20% of its size.
	xmax2 += 0.2*model_wid2;
	ymin2 -= 0.2*model_hgt2;
	ymax2 += 0.2*model_hgt2;
	zmin2 -= 0.2*model_hgt2;
	zmax2 += 0.2*model_hgt2;  

	//~ 

	//~ 
    glLightfv(GL_LIGHT0, GL_AMBIENT, grey);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, white);
    glLightfv(GL_LIGHT0, GL_SPECULAR, white);
    
    glLightfv(GL_LIGHT1, GL_AMBIENT, grey);
    glLightfv(GL_LIGHT1, GL_DIFFUSE, white);
    glLightfv(GL_LIGHT1, GL_SPECULAR, white);
    glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 20.0);
    glLightf(GL_LIGHT1, GL_SPOT_EXPONENT,5.0);
    //~ 
    //~ glLightfv(GL_LIGHT2, GL_AMBIENT, grey);
    //~ glLightfv(GL_LIGHT2, GL_DIFFUSE, white);
    //~ glLightfv(GL_LIGHT2, GL_SPECULAR, white);


		gluQuadricDrawStyle (q, GLU_FILL );
		gluQuadricNormals	(q, GLU_SMOOTH );
		
		   glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
    glEnable(GL_COLOR_MATERIAL);
    
    glMaterialfv(GL_FRONT, GL_SPECULAR, white);
    glMaterialf(GL_FRONT, GL_SHININESS, 50);	


	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	

	//glFrustum(-5, 5, -5, 5, 5, far);  //The camera view volume  
	gluPerspective(zoomFactor, 1, near, far);  //The camera view volume
	initializeHalos();  
	initializeHaloColor();
	initializeAliens();
	
	//$0
	
	
}

void display()  
{	

   //$0

	float lpos[4] =	
	{100., 100., 100., 1.0};  //light's position
	//~ {0.0f, 50.0f, 0.0f, 1.0f}; 



	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);    //GL_LINE = Wireframe;   GL_FILL = Solid
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	
	gluLookAt(cam_x, cam_hgt, cam_zoom, cam_x_walk, 0, 0, 0, 1, 0);
	glLightfv(GL_LIGHT0, GL_POSITION, lpos);   //set light position
	


	glRotatef(angle, 0.0, 1.0, 0.0);		//rotate the whole scene
	


	drawSignalPost();
	drawPath();
	displayStars();
	drawFloor();
//~ 
	//~ glPushMatrix();
		//~ 
	//~ glTranslatef(zackPos[0][0],zackPos[0][1], zackPos[0][2]);
	//~ glRotatef(zack_turn, 0.0, 1.0, 0.0);
	//~ glTranslatef(-1*zackPos[0][0],-1*zackPos[0][1], -1*zackPos[0][2]);
	//~ displayZack(0);
		drawTeapot(0);

	//~ glPopMatrix();
	//~ 

	
	//~ glPushMatrix();
	//~ glTranslatef(zackPos[3][0],zackPos[3][1], zackPos[3][2]);
	//~ glRotatef(zack_turn, 0.0, 1.0, 0.0);
	//~ glTranslatef(-1*zackPos[3][0],-1*zackPos[3][1], -1*zackPos[3][2]);
//~ displayZack(3);
	//~ glPopMatrix();
	
	displayZack(0);
	displayZack(1);
	displayZack(2);
	displayZack(3);

		//~ 
		//~ glPushMatrix();
		//~ glTranslatef(turning_point[0][0],turning_point[0][1], turning_point[0][2]);
		//~ glRotatef(zack_turn, 0.0, 1.0, 0.0);
	//~ glTranslatef(-1*turning_point[0][0],-1*turning_point[0][1], -1*turning_point[0][2]);
	//~ glPopMatrix();
	drawFloor();
	drawLand(3,10);
	if(door_open){
		displayHaloOnBoard(0);
		displayHaloOnBoard(1);
		displayHaloOnBoard(2);
		displayHaloOnBoard(3);
		displayHaloOnBoard(4);	
		displayHaloOnBoard(5);	
		displayHaloOnBoard(6);		
	}

	displaySpaceship();

	
	if(door_open){

			//~ glutSwapBuffers();
		drawTeapot(1);
			//~ glutSwapBuffers();
		drawTeapot(2);
		
	}
    
	//displayHalo();
	//~ if(wantRedisplay[0]){
	//~ 

	
	//~ displayHalo();
	//	displayHalos(halo_len);


	 //~ displayHaloOnBoard2();


	//$0		
	glutSwapBuffers();

	glFlush();
}



//------------ Special key event callback ---------------------------------
// To enable the use of left and right arrow keys to rotate the scene
void special(int key, int x, int y)
{
   if(key == GLUT_KEY_LEFT) angle-= 5; printf("%f angle\n", angle);
           if(key == GLUT_KEY_RIGHT) angle += 5; printf("%f angle\n", angle);
        if(key == GLUT_KEY_F1) 
         if(zoomFactor < 100){
				 zoomFactor+= 5;
				  setProjectionMatrix();
				 }
				 
	    if(key == GLUT_KEY_F2) 		
	    if(zoomFactor > 5){
				 zoomFactor-= 5;
				  setProjectionMatrix();
				 }

		if(key == GLUT_KEY_UP) {
			cam_hgt= cam_hgt +4;
			 //~ if(zoomFactor < 100){
				 //~ zoomFactor+= 5;
				  //~ setProjectionMatrix();
				 //~ }
				 
			 }
		if(key == GLUT_KEY_DOWN)
		{ 		 
			cam_hgt= cam_hgt - 4;
			//~ if(zoomFactor > 5){
				 //~ zoomFactor-= 5;
				  //~ setProjectionMatrix();
				 //~ }
		}
		if(key == GLUT_KEY_F5) cam_zoom++;
		else if(key == GLUT_KEY_F6) cam_zoom--;
				if(key == GLUT_KEY_F7) cam_x++;
		else if(key == GLUT_KEY_F8) cam_x--;
						//~ if(key == GLUT_KEY_F9) cam_x_walk++;
		//~ else if(key == GLUT_KEY_F10) cam_x_walk--;
		
							if(key == GLUT_KEY_F9) xx = xx-0.5;
		else if(key == GLUT_KEY_F10) xx = xx+0.5;
									if(key == GLUT_KEY_F11) yy=yy-0.5;
		else if(key == GLUT_KEY_F12) yy=yy+0.5;
		if(key == GLUT_KEY_F4){


}
    glutPostRedisplay();
}

void resetArray(int array[]){
	for(int i = 0; i < 4; i++){
		array[i] = 0;
		}
	}

void keyboard(unsigned char key, int x, int y)
{
	resetArray(direction);
	
	    switch (key) 
    {    
	
       //glutTimerFunc(20, haloJumpUpDown, 0); 
      // glutTimerFunc(1000, openShelter, 0);   
       case 'a' :  halo_jump = true;direction[2] = 1; glutTimerFunc(20, haloJump, 0);   break; //east
       case 'w':  halo_jump = true; direction[0] = 1; glutTimerFunc(20, haloJump, 0);  break; //north
       case 's' :  halo_jump = true; direction[1] = 1; glutTimerFunc(20, haloJump, 0);  break; //south
		case 'd' :  halo_jump = true; direction[3] = 1; glutTimerFunc(20, haloJump, 0); break; //west
       case 'o' :       glutTimerFunc(300, openDoor, 0); break; //open door
       //zackPos[0][0] = -85; zackPos[0][2] = -25;
     case 'l' :      glutTimerFunc(300, moveSpaceship, 0); break; //open door
       case 'x' :      glutTimerFunc(100, zackTurnFront, 3); break; //open door
 
          case 'c' :   glutTimerFunc(300, zackTurnEnd, 3); break; //open door
             case 'v' :   glutTimerFunc(50, zackWalkR, 0); break; //open door
    case 'u' :   glutTimerFunc(100, zackRotateHead, 3); break; //open door
    case 'n' :   glutTimerFunc(50, haloOnBoardOut, stage);  break; //open door
          	      
     case 'm' :   want_teapot = true;  glutTimerFunc(50, haloOnBoardOut, stage);break; //open door
          	      
          	            	      //~ case 'k' :   far = far-10;; break;
          	            	         //~ case 'l' :   far= far+10;break;
          	            	               //~ case 'h' :   near=near-10; break;
          	            	         //~ case 'j' :   near=near+10; 
          	            	         //~ printf("He");break;
          	            	         
          	            	     }
      glutPostRedisplay();
    }
//  ------- Main: Initialize glut window and register call backs -----------
int main(int argc, char** argv)
{
   glutInit(&argc, argv);
   glutInitDisplayMode (GLUT_DOUBLE | GLUT_DEPTH);
   glutInitWindowSize (600, 600); 
   glutInitWindowPosition (10, 10);
   glutCreateWindow ("Model3D");
   initialize();
	glutTimerFunc(50, timer, 0);
	
//glutTimerFunc(50, moveSpaceship, 0);


//~ glutTimerFunc(300, zackWalkL, 1);
	
glutTimerFunc(300, zackWalkL, 2);
	//~ glutTimerFunc(300, zackWalkL, 3);
glutKeyboardFunc(keyboard);

   glutDisplayFunc(display);
   glutSpecialFunc(special); 
   glutMainLoop();
   return 0;
}
