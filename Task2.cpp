//  ========================================================================
//  COSC422: Advanced Computer Graphics;  University of Canterbury (2017)
//
//  FILE NAME: ModelLoader.cpp
//
//  This is a modified version of the sample program included with the Assimp library
//    from http://www.assimp.org/main_downloads.html
//
//	Includes only basic functions (no texture mapping or skeletal animations)
//  Press key '1' to toggle 90 degs model rotation about x-axis on/off.
//  See Ex10.pdf for details.
//  ========================================================================

#include <iostream>
#include <fstream>
#include <GL/freeglut.h>

using namespace std;

#include <assimp/cimport.h>
#include <assimp/types.h>
#include <assimp/scene.h>
#include <vector>
#include <assimp/postprocess.h>
#include "assimp_extras.h"

const aiScene* scene = NULL;
float angle = 90;
aiVector3D scene_min, scene_max, scene_center;
ofstream fileout;

aiVector3D* verts;
aiVector3D* norm;

int tick = 0;
int updateTime = 3;
float lookOffX = 0;
float lookOffY = 3;
float lookOffZ = 8;
float rOffX = 0;
float rOffY = 0;
float rOffZ = 0;
float movedDist = 0;
int movedToken = 0;
float rockAngle = 0;
float rockZ = 0;
float initialTickPerSec = 5000;
const float RUNNING = 92;
bool stop = false;



// ------Load scene and model----------
bool loadModel(const char* fileName)
{
	scene = aiImportFile(fileName, aiProcessPreset_TargetRealtime_Quality);
	if(scene == NULL) exit(1);
	printSceneInfo(fileout, scene);
	printTreeInfo(fileout, scene->mRootNode);
	printAnimInfo(fileout, scene);
	get_bounding_box(scene, &scene_min, &scene_max);
	return true;
}
//----Create floor in a scene----
void createFloor(){
    glEnable(GL_COLOR_MATERIAL);
    glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
    glColor3f(0.0f, 1.0f, 0.0f);
	glPushMatrix();
	
    glBegin(GL_QUADS);
    glVertex3f(-1000, -10, -1000);
    glVertex3f(-1000, -10, 1000);
    glVertex3f(1000, -10, 1000);
    glVertex3f(1000, -10, -1000);

    glEnd();
    glPopMatrix();
    glDisable(GL_COLOR_MATERIAL);
}

//----Create gate for Brisca home----
void createWall(float z){

    glEnable(GL_COLOR_MATERIAL);
    glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
    glColor3f(1,0,0);
    
	glPushMatrix();
    glScalef(3, 0.2f, 0.7);
    glTranslatef(0,15,z);
	glutSolidCube(5);
	glPopMatrix();

	
	glPushMatrix();
    glScalef(1, 1.2f, 0.7);
    glTranslatef(4.5,0,z);
	glutSolidCube(5);
	glPopMatrix();

	
	glPushMatrix();
    glScalef(1, 1.2f, 0.7);
    glTranslatef(-4.5,0,z);
	glutSolidCube(5);
	glPopMatrix();
	
	//right
	glPushMatrix();
    glScalef(40, 1.2f, 0.7);
    glTranslatef(-2.75,0,z);
	glutSolidCube(5);
	glPopMatrix();
	
	glPushMatrix();
    glScalef(40, 1.2f, 0.7);
    glTranslatef(2.75,0,z);
	glutSolidCube(5);
	glPopMatrix();
	
	glDisable(GL_COLOR_MATERIAL);
	
}

//----Create rolling rocks----
void createRock(float size, float x){
	glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
    glColor3f(0.64,0.16,0.16);

	glPushMatrix();
		glTranslatef(0,0,rockZ);
		glPushMatrix();
			glTranslatef(x,1,-10);
			glRotatef(rockAngle, 1, 0, 0);
			glScalef(size, size, size);
			glutSolidDodecahedron();
		glPopMatrix();
	glPopMatrix();
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
				else
					glDisable(GL_COLOR_MATERIAL);
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
	loadModel("Model Files/wuson.x");		//<<<-------------Specify input file name here  --------------
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(45, 1, 1.0, 1000.0);
	
	aiMesh* mesh;
		
	int index = 0;
	int vertSize = 0;
	
	for (uint n = 0; n < scene->mNumMeshes; n++)
	{
		vertSize += scene->mMeshes[n]->mNumVertices;
	}
	

	verts = new aiVector3D[vertSize];
	norm = new aiVector3D[vertSize];

	for (uint n = 0; n < scene->mNumMeshes; n++)
	{
	
		mesh = scene->mMeshes[n];

		for (uint iv = 0; iv < mesh->mNumVertices; iv++)
		{
			verts[index] = mesh->mVertices[iv];
			norm[index] =  mesh->mNormals[iv];

			index++;
		}
		
	}
}

void moveBison(){
	
	if(movedDist <= RUNNING){
		movedDist += 0.1;
		lookOffX += 0.025;
		rOffX += 0.030;	
		
		if(movedDist <= 79){
			rockAngle++;
			if(movedDist >= 50){
				rockZ += 0.115;
			} else{
				rockZ += 0.1; //increase speed half way through
			}
		}
	} 
	

}

//----Update nodes position and rotation----
void updateNodes() {
	aiMatrix4x4 matPos;
	aiMatrix4x4 matRotn3;
	aiMatrix4x4 matprod;
	aiVector3D posn;
	aiQuaternion rotn;
	aiNodeAnim *chnl;
	aiAnimation *anim = scene->mAnimations[0];

	int time = glutGet(GLUT_ELAPSED_TIME);
				
	double tickPerSec = anim->mTicksPerSecond;

	tickPerSec = initialTickPerSec;
	
	tick = (time * tickPerSec)/1000;
	tick = tick % (int) anim->mDuration;

	//get motion data and replace matrix with it
	for (uint i = 0; i < anim->mNumChannels; i++){
		chnl = anim->mChannels[i];
		
		
		if(chnl->mNumPositionKeys == 1){
			posn = chnl->mPositionKeys[0].mValue;
		} else {
			for(uint index = 1; index < chnl->mNumPositionKeys; index++){
				if(chnl->mPositionKeys[index-1].mTime < tick && tick <= chnl->mPositionKeys[index].mTime){
					aiVector3D posn1 = (chnl->mPositionKeys[index-1]).mValue; 
					aiVector3D posn2 = (chnl->mPositionKeys[index]).mValue;
					
					float time1 = (chnl->mPositionKeys[index-1]).mTime; //x
					float time2 = (chnl->mPositionKeys[index]).mTime; 
					
					float factor = (tick-time1)/(time2-time1);
					
					posn = (posn1 * (1-factor)) + (posn2 * factor);

					moveBison();
					break;
					
				}
			} 
	
		}


		if(chnl->mNumRotationKeys == 1){
			rotn = chnl->mRotationKeys[0].mValue;

		} else {
			for(uint index = 1; index < chnl->mNumRotationKeys; index++){
				if(chnl->mRotationKeys[index-1].mTime < tick && tick <= chnl->mRotationKeys[index].mTime){
					rotn = chnl->mRotationKeys[tick].mValue;
					aiQuaternion rotn1 = (chnl->mRotationKeys[index-1]).mValue;
					aiQuaternion rotn2 = (chnl->mRotationKeys[index]).mValue;
					double time1 = (chnl->mRotationKeys[index-1]).mTime;
					double time2 = (chnl->mRotationKeys[index]).mTime;
					double factor = (tick-time1)/(time2-time1);
					rotn.Interpolate(rotn, rotn1, rotn2, factor);
					break;
				}
			} 
			
		
		}
		
		if(movedDist <= RUNNING){ //if passed the gate for a while
			matPos.Translation(posn, matPos);
			aiMatrix3x3 matRotn3 = rotn.GetMatrix();
			aiMatrix4x4 matRot = aiMatrix4x4(matRotn3);
			matprod = matPos * matRot;
			
			aiNode* node = scene->mRootNode->FindNode(chnl->mNodeName);
			node->mTransformation = matprod;
		}
	}
}

//----Update vertices and normals----
void updateMeshes() {

	aiMatrix4x4 Bprod;
	aiMatrix4x4 m;
	int off = 0;

	for (uint n = 0; n < scene->mNumMeshes; n++)
	{
		aiMesh* mesh = scene->mMeshes[n];
		
		for (uint bi = 0; bi < mesh->mNumBones; bi++)
		{
			aiBone* bone = mesh->mBones[bi];
			Bprod = bone->mOffsetMatrix;

			aiNode* currentNode = scene->mRootNode->FindNode(bone->mName); //Q0

			while(currentNode != scene->mRootNode){ //Q1-QR	 
				Bprod = currentNode->mTransformation * Bprod;
				currentNode = currentNode->mParent;		
			}
				
			//when mRootNode
			Bprod = currentNode->mTransformation * Bprod;
				
			aiMatrix4x4 D = Bprod;
			D = D.Inverse().Transpose(); 
			
			int vid;
			
			for (uint k = 0; k < bone->mNumWeights; k++)
			{
				vid = (bone->mWeights[k]).mVertexId;
	
				mesh->mVertices[vid] = Bprod * verts[vid+off];
				mesh->mNormals[vid] = D * norm[vid+off]; 
			}
		}

		off = off + mesh->mNumVertices; //can move pointer to the next set of vertices
	
	}
}
//----Timer callback for continuous rotation of the model about y-axis----
void update(int value)
{
	if(!stop){
		updateNodes();
		
		updateMeshes();	

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

//------The main display function---------
//----The model is first drawn using a display list so that all GL commands are
//    stored for subsequent display updates.
void display()
{
	float pos[4] = {-50, 50, 2, 1};
	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	
	gluLookAt(lookOffX, lookOffY, lookOffZ, rOffX, rOffY, rOffZ, 0, 1, 0);
	glLightfv(GL_LIGHT0, GL_POSITION, pos);
	
	glRotatef(angle, 0.f, 1.f ,0.f);  //Continuous rotation about the y-axis


	float tmp = scene_max.x - scene_min.x;
	tmp = aisgl_max(scene_max.y - scene_min.y,tmp);
	tmp = aisgl_max(scene_max.z - scene_min.z,tmp);
	tmp = 1.f / tmp;
	
	glScalef(tmp, tmp, tmp);

	//other objects
	glPushMatrix();

	glTranslatef(0,-9,0); //move to same height as base
	createRock(2.1, 0);
	createRock(1.8,-10);
	createRock(1.3,10);
	

	createWall(110);
	
	glPopMatrix();
	
	
	glPushMatrix();	
		createFloor();
		glTranslatef(0,0,movedDist); //move the model
		//model
		glPushMatrix();
			glTranslatef(0,-10,0); // move the model down to the base
			glPushMatrix();
				glRotatef(90, 1, 0, 0);	//rotate model so leg is on the base
				render(scene, scene->mRootNode);
			glPopMatrix();
		glPopMatrix();
	glPopMatrix();
	 

	glutSwapBuffers();
}



int main(int argc, char** argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(600, 600);
	glutCreateWindow("Skeleton Animation Direct X - Assignment 2");
	glutInitContextVersion (4, 2);
	glutInitContextProfile ( GLUT_CORE_PROFILE );

	initialise();
	glutDisplayFunc(display);
	glutTimerFunc(updateTime, update, 0);
	glutKeyboardFunc(keyboard);
	glutMainLoop();

	aiReleaseImport(scene);
}

