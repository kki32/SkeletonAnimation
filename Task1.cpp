//  ========================================================================
//  COSC422: Advanced Computer Graphics;  University of Canterbury (2017)
//
//  FILE NAME: Task1.cpp
//
//  This is a modified version of the sample program included with the Assimp library
//    from http://www.assimp.org/main_downloads.html
//	This function is modified from Exercise 10
//  ========================================================================

#include <iostream>
#include <fstream>
#include <GL/freeglut.h>

using namespace std;

#include <assimp/cimport.h>
#include <assimp/types.h>
#include <assimp/scene.h>
#include <assimp/postprocess.h>
#include "assimp_extras.h"

const aiScene* scene = NULL;
float angle = 13;
aiVector3D scene_min, scene_max, scene_center;
ofstream fileout;

int tick = 0;
int updateTime = 40;
int lookOffX = 2;
int lookOffY = 3;
int lookOffZ = 2;
bool stop = false;

// ------Load scene and model----------
bool loadModel(const char* fileName)
{
	scene = aiImportFile(fileName, aiProcess_Debone);
	if(scene == NULL) exit(1);
	printSceneInfo(fileout, scene);
	printTreeInfo(fileout, scene->mRootNode);
	printAnimInfo(fileout, scene);
	get_bounding_box(scene, &scene_min, &scene_max);
	return true;
}

// ------A recursive function to traverse scene graph and render each mesh----------
void render (const aiScene* sc, const aiNode* nd)
{
	aiMatrix4x4 m = nd->mTransformation;
	aiTransposeMatrix4(&m);   //Convert to column-major order
	glPushMatrix();
	glMultMatrixf((float*)&m);   //Multiply by the transformation matrix for this node

	aiMesh* mesh;
	aiFace* face;


	// Draw all meshes assigned to this node
	for (uint n = 0; n < nd->mNumMeshes; n++)
	{
		mesh = scene->mMeshes[nd->mMeshes[n]];
	
		apply_material(sc->mMaterials[mesh->mMaterialIndex]);

		if(mesh->HasNormals())
			glEnable(GL_LIGHTING);
		else
			glDisable(GL_LIGHTING);


		if(mesh->HasVertexColors(0))
			glEnable(GL_COLOR_MATERIAL);
		else
			glDisable(GL_COLOR_MATERIAL);

		//Get the polygons from each mesh and draw them
		for (uint k = 0; k < mesh->mNumFaces; k++)
		{
			face = &mesh->mFaces[k];
			GLenum face_mode;

			switch(face->mNumIndices)
			{
				case 1: face_mode = GL_POINTS; break;
				case 2: face_mode = GL_LINES; break;
				case 3: face_mode = GL_TRIANGLES; break;
				default: face_mode = GL_POLYGON; break;
			}

			glBegin(face_mode);

			for(uint i = 0; i < face->mNumIndices; i++) {
				int index = face->mIndices[i];
				if(mesh->HasVertexColors(0))
				{
					glEnable(GL_COLOR_MATERIAL);
					glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
					
					glColor4fv((GLfloat*)&mesh->mColors[0][index]);
				}
				else{
					glDisable(GL_COLOR_MATERIAL);
				}
				if(mesh->HasNormals())
					glNormal3fv(&mesh->mNormals[index].x);
				glVertex3fv(&mesh->mVertices[index].x);
			}

			glEnd();
		}

	}


			
	// Draw all children
	for (uint i = 0; i < nd->mNumChildren; i++)
		render(sc, nd->mChildren[i]);


	glPopMatrix();
}

//--------------------OpenGL initialization------------------------
void initialise()
{
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	glEnable(GL_LIGHTING);
	
	glEnable(GL_LIGHT0);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_NORMALIZE);
	glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
	glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
	fileout.open("BVH_Files/sceneInfo.txt", ios::out);
	loadModel("BVH_Files/01_01.bvh");

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(45, 1, 1.0, 1000.0);
}

//----Update nodes position and rotation----
void updateNodes() {
	aiMatrix4x4 matPos;
	aiMatrix4x4 matRotn3;
	aiMatrix4x4 matprod;
	aiNodeAnim *chnl;
	aiVector3D posn;
	aiQuaternion rotn;
	
	aiAnimation *anim = scene->mAnimations[0];
	
	int time = glutGet(GLUT_ELAPSED_TIME);
				
	double tickPerSec = anim->mTicksPerSecond;

	
	//get motion data and replace matrix with it
	for (uint i = 0; i < anim->mNumChannels; i++){
		tick = (time * tickPerSec)/1000;
		tick = tick % (int) anim->mDuration;
		chnl = anim->mChannels[i];

		if(chnl->mNumPositionKeys == 1){
			posn = chnl->mPositionKeys[0].mValue;
		} else {
				posn = chnl->mPositionKeys[tick].mValue;	
		}


		if(chnl->mNumRotationKeys == 1){
			rotn = chnl->mRotationKeys[0].mValue;
		} else {
			rotn = chnl->mRotationKeys[tick].mValue; 	
		}

		matPos.Translation(posn, matPos);
		aiMatrix3x3 matRotn3 = rotn.GetMatrix();
		aiMatrix4x4 matRot = aiMatrix4x4(matRotn3);
		matprod = matPos * matRot;
		
		aiNode* node = scene->mRootNode->FindNode(chnl->mNodeName);
		node->mTransformation = matprod;
	}
}


//----Timer callback for continuous rotation of the model about y-axis----
void update(int value)
{
	if(!stop){
		updateNodes();
		render(scene, scene->mRootNode);
	}
			
	glutPostRedisplay();
	glutTimerFunc(updateTime, update, 0);
	
	

}

//----Keyboard callback to toggle initial model orientation---
void keyboard(unsigned char key, int x, int y)
{
	if(key == '2'){
		angle++;
		if(angle > 360) angle = 0;
	} 

	if(key == '3'){
		lookOffX = lookOffX + 1;
	}
	if(key == '4'){
		lookOffX = lookOffX - 1;
	}
	if(key == '5'){
		lookOffY = lookOffY + 1;
	}
	if(key == '6'){
		lookOffY = lookOffY - 1;
	}
	if(key == '7'){
		lookOffZ = lookOffZ + 1;
	}
	if(key == '8'){
		lookOffZ = lookOffZ - 1;
	}
	if(key == 's'){
		stop = !stop;
	}
	glutPostRedisplay();
}

//----Create floor----
void createFloor(){
    glEnable(GL_COLOR_MATERIAL);
	glPushMatrix();
    glBegin(GL_QUADS);

    glColor3f(0.64,0.16,0.16);
    glVertex3f(-1000, -10, -1000);
    glVertex3f(-1000, -10, 1000);
    glVertex3f(1000, -10, 1000);
    glVertex3f(1000, -10, -1000);
    
    glEnd();
    glPopMatrix();
    glDisable(GL_COLOR_MATERIAL);

}
	
//----Create wall for a maze----
void createWall(){

    glEnable(GL_COLOR_MATERIAL);

    //back wall
    glPushMatrix();
    glTranslatef(0,0,65);
    glScalef(8.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	
	//back
	glPushMatrix();
	glTranslatef(-10,0,-40);
    glScalef(8.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();

	//side l
	glPushMatrix();
	glTranslatef(-10,0,25);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(11.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//side r
	glPushMatrix();
	glTranslatef(30,0,15);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(15.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//side r
	glPushMatrix();
	glTranslatef(60,0,15);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(15.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//front
	glPushMatrix();
	glTranslatef(0,0,100);
    glScalef(20.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	
	//front
	glPushMatrix();
	glTranslatef(40,0,190);
    glScalef(20.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	
	//front front
	glPushMatrix();
	glTranslatef(90,0,100);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(20.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//front rotate
	glPushMatrix();
	glTranslatef(60,0,100);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(20.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//side r
	glPushMatrix();
	glTranslatef(60,0,50);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(15.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	
	//side r
	glPushMatrix();
	glTranslatef(60,0,50);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(15.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//side l
	glPushMatrix();
	glTranslatef(-45,0,25);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(11.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//side l
	glPushMatrix();
	glTranslatef(-45,0,90);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(11.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//front rotate
	glPushMatrix();
	glTranslatef(-20,0,100);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(20.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	//front rotate
	glPushMatrix();
	glTranslatef(-90,0,160);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
    glScalef(20.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	
	//border back
	glPushMatrix();
	glTranslatef(0,0,-50);
    glScalef(50.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	
	//border front
	glPushMatrix();
	glTranslatef(0,0,250);
    glScalef(50.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	
	glPushMatrix();
	glTranslatef(-130,0,130);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
	glScalef(100.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	
	glPushMatrix();
	glTranslatef(100,0,85);
	glPushMatrix();
  	glRotatef(90, 0, 1, 0);
	glScalef(50.0f, 10.0f, 2.0f);
	glutSolidCube(5);
	glPopMatrix();
	glPopMatrix();
	
	glDisable(GL_COLOR_MATERIAL);
}

//------The main display function---------
//----The model is first drawn using a display list so that all GL commands are
//    stored for subsequent display updates.
void display()
{
	aiQuaterniont<float> quat; 
	aiVector3t<float> look;
	
	float pos[4] = {10, 50, -18, 1};
	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	
	// scale the whole asset to fit into our view frustum
	float tmp = scene_max.x - scene_min.x;
	tmp = aisgl_max(scene_max.y - scene_min.y,tmp);
	tmp = aisgl_max(scene_max.z - scene_min.z,tmp);
	tmp = 1.f / tmp;
	
	scene->mRootNode->mTransformation.DecomposeNoScaling(quat, look);
	look = tmp * look;
	
	gluLookAt(look.x+lookOffX, look.y+lookOffY, look.z+lookOffZ, look.x, look.y, look.z, 0, 1, 0);
	glLightfv(GL_LIGHT0, GL_POSITION, pos);
	
	glRotatef(angle, 0.f, 1.f ,0.f);  //Continuous rotation about the y-axis

	glScalef(tmp, tmp, tmp);
	
	render(scene, scene->mRootNode);

	//create other objects
	createFloor();
	createWall();

	glutSwapBuffers();
}



int main(int argc, char** argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(600, 600);
	glutCreateWindow("Skeleton Animation BVH - Assignment 2");
	glutInitContextVersion (4, 2);
	glutInitContextProfile ( GLUT_CORE_PROFILE );

	initialise();
	glutDisplayFunc(display);
	glutTimerFunc(updateTime, update, 0);
	glutKeyboardFunc(keyboard);
	glutMainLoop();

	aiReleaseImport(scene);
}

